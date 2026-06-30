package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import com.battilana.onepage.entity.BancoEntity;
import com.battilana.onepage.entity.PagoVigenteEntity;
import com.battilana.onepage.exception.FormatoArchivoNoValidoException;
import com.battilana.onepage.repository.BancoRepository;
import com.battilana.onepage.repository.PagoVigenteRepository;
import com.battilana.onepage.service.PagoVigenteService;
import com.battilana.onepage.util.HomologacionEstadoVigente;
import com.battilana.onepage.util.parser.BancoVigenteParser;
import com.battilana.onepage.util.parser.ScotiabankVigenteParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoVigenteServiceImpl implements PagoVigenteService {
    private final PagoVigenteRepository pagoVigenteRepository;
    private final ScotiabankVigenteParser scotiabankVigenteParser;
    private final BancoRepository bancoRepository;

    @Override
    @Transactional
    public void registrarPagosVigentes(MultipartFile archivo, String codigoBanco) {
        log.info("Carga de pagos vigentes: {} para banco {}", archivo.getOriginalFilename(), codigoBanco);

        BancoEntity banco = bancoRepository.findByCodigo(codigoBanco)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado: " + codigoBanco));

        BancoVigenteParser parser = seleccionarParser(codigoBanco);

        List<PagoVigenteNormalizadoDto> normalizados;
        try (Workbook workbook = WorkbookFactory.create(archivo.getInputStream())) {

            if (!parser.coincideFormato(workbook)) {
                throw new FormatoArchivoNoValidoException(
                        "El archivo '" + archivo.getOriginalFilename() +
                                "' no corresponde al formato del banco " + codigoBanco + ".");
            }

            normalizados = parser.parsear(workbook);

        } catch (FormatoArchivoNoValidoException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel: " + e.getMessage(), e);
        }

        if (normalizados.isEmpty()) {
            log.warn("No se encontraron registros vigentes en el archivo");
            return;
        }

        String nombreArchivo = archivo.getOriginalFilename();
        List<PagoVigenteEntity> entities = normalizados.stream()
                .map(dto -> {
                    PagoVigenteEntity e = new PagoVigenteEntity();
                    e.setBancoEntity(banco);
                    e.setNroUnico(dto.nroUnico());
                    e.setNroFactura(dto.nroFactura());
                    e.setAceptante(dto.aceptante());
                    e.setFechaIngreso(dto.fechaIngreso());
                    e.setFechaVencimiento(dto.fechaVencimiento());
                    e.setMoneda(dto.moneda());
                    e.setImporte(dto.importe());
                    e.setEstadoOriginal(dto.estadoOriginal());
                    e.setEstado(HomologacionEstadoVigente.homologar(dto.estadoOriginal()));
                    e.setArchivoOrigen(nombreArchivo);
                    return e;
                })
                .toList();

        pagoVigenteRepository.saveAll(entities);
        log.info("Se registraron {} pagos vigentes del banco {}", entities.size(), codigoBanco);
    }

    private BancoVigenteParser seleccionarParser(String codigoBanco) {
        return switch (codigoBanco.toUpperCase()) {
            case "SCOTIA", "SCOTIABANK" -> scotiabankVigenteParser;
            default -> throw new RuntimeException("Parser de vigentes no implementado para: " + codigoBanco);
        };
    }
}
