package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import com.battilana.onepage.dto.pago.PagoResponse;
import com.battilana.onepage.entity.BancoEntity;
import com.battilana.onepage.entity.PagoEntity;
import com.battilana.onepage.mappers.PagoMapper;
import com.battilana.onepage.repository.BancoRepository;
import com.battilana.onepage.repository.PagoRepository;
import com.battilana.onepage.service.PagoService;
import com.battilana.onepage.util.HomologacionEstado;
import com.battilana.onepage.util.parser.BancoParser;
import com.battilana.onepage.util.parser.BbvaParser;
import com.battilana.onepage.util.parser.BcpParser;
import com.battilana.onepage.util.parser.ScotiabankParser;
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
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final BancoRepository bancoRepository;
    private final BbvaParser bbvaParser;
    private final BcpParser bcpParser;
    private final ScotiabankParser scotiabankParser;
    private final PagoMapper pagoMapper;

    @Override
    public List<PagoResponse> listar() {
        return this.pagoMapper.toListPagoResponse(this.pagoRepository.findAllByOrderByIdPagoDesc());
    }

    @Override
    @Transactional
    public void registrarDocumentos(MultipartFile archivo, String codigoBanco) {
        log.info("Iniciando carga de archivo: {} para banco: {}", archivo.getOriginalFilename(), codigoBanco);

        // 1. Buscar el banco en BD
        BancoEntity banco = bancoRepository.findByCodigo(codigoBanco)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado: " + codigoBanco));

        // 2. Seleccionar el parser según el banco
        BancoParser parser = seleccionarParser(codigoBanco);

        // 3. Leer el Excel con Apache POI
        List<PagoNormalizadoDto> pagosNormalizados;
        try (Workbook workbook = WorkbookFactory.create(archivo.getInputStream())) {
            pagosNormalizados = parser.parsear(workbook);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel: " + e.getMessage(), e);
        }

        if (pagosNormalizados.isEmpty()) {
            log.warn("No se encontraron registros en el archivo");
            return;
        }

        // 4. Convertir a entidades
        String nombreArchivo = archivo.getOriginalFilename();
        List<PagoEntity> entities = pagosNormalizados.stream()
                .map(dto -> {
                    PagoEntity entity = new PagoEntity();
                    entity.setBancoEntity(banco);
                    entity.setNroTransaccion(dto.nroTransaccion());
                    entity.setNroFactura(dto.nroFactura());
                    entity.setAceptante(dto.aceptante());
                    entity.setFechaOperacion(dto.fechaOperacion());
                    entity.setFechaVencimiento(dto.fechaVencimiento());
                    entity.setFechaIngresoBanco(dto.fechaIngresoBanco());
                    entity.setMoneda(dto.moneda());
                    entity.setImporteIngreso(dto.importeIngreso());
                    entity.setImporte(dto.importe());
                    entity.setInteres(dto.interes());
                    entity.setComision(dto.comision());
                    entity.setGastos(dto.gastos());
                    entity.setEstadoOriginal(dto.estadoOriginal());
                    entity.setEstado(HomologacionEstado.homologar(dto.estadoOriginal()));
                    entity.setArchivoOrigen(nombreArchivo);
                    return entity;
                })
                .toList();

        // 5. Guardar (el unique constraint protege contra duplicados)
        pagoRepository.saveAll(entities);
        log.info("Se registraron {} pagos del banco {}", entities.size(), codigoBanco);
    }

    private BancoParser seleccionarParser(String codigoBanco) {
        return switch (codigoBanco.toUpperCase()) {
            case "BBVA" -> bbvaParser;
            case "BCP" -> bcpParser;
            case "SCOTIA" -> scotiabankParser;
            default -> throw new RuntimeException("Parser no implementado para: " + codigoBanco);
        };
    }
}
