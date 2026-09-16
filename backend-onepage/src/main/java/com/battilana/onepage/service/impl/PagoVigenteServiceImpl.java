package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import com.battilana.onepage.dto.pago.PagoVigenteResponse;
import com.battilana.onepage.entity.BancoEntity;
import com.battilana.onepage.entity.PagoVigenteEntity;
import com.battilana.onepage.exception.FormatoArchivoNoValidoException;
import com.battilana.onepage.mappers.PagoVigenteMapper;
import com.battilana.onepage.repository.BancoRepository;
import com.battilana.onepage.repository.PagoVigenteRepository;
import com.battilana.onepage.service.PagoVigenteService;
import com.battilana.onepage.util.HomologacionEstadoVigente;
import com.battilana.onepage.util.parser.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoVigenteServiceImpl implements PagoVigenteService {

    /**
     * VARIABLES ESTATICAS
     */
    private static final String LETRA = "Descuento de Letra de Corto Plazo";
    private static final String FACTURA_NEGOCIABLE = "Descuento de Factura Negociable Electrónica";
    private static final String COBRANZA_LIBRE = "Cobranza de Factura Negociable Electrónica";

    private final PagoVigenteRepository pagoVigenteRepository;
    private final ScotiabankVigenteParser scotiabankVigenteParser;
    private final BancoRepository bancoRepository;
    private final PagoVigenteMapper pagoVigenteMapper;
    private final BbvaVigenteParser bbvaVigenteParser;
    private final BcpVigenteParser bcpVigenteParser;
    private final InterbankVigenteParser interbankVigenteParser;

    @Override
    public List<PagoVigenteResponse> listado() {
        return this.pagoVigenteMapper.toPagoVigenteResponseList(this.pagoVigenteRepository.findAll());
    }

    @Override
    @Transactional
    public void registrarPagosVigentes(MultipartFile archivo, String codigoBanco) {

        BancoEntity banco = bancoRepository.findByCodigo(codigoBanco)
                .orElseThrow(() -> new RuntimeException("Banco no encontrado: " + codigoBanco));

        BancoParser<PagoVigenteNormalizadoDto> parser = seleccionarParser(codigoBanco);

        List<PagoVigenteNormalizadoDto> normalizados;
        try (Workbook workbook = WorkbookLoader.cargar(archivo)) {

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

        switch (codigoBanco.toUpperCase()){
            case "BCP" -> {
                String productoNormalizado = removerAcento(normalizados.getLast().producto());
                switch (productoNormalizado){
                    case "Cobranza de Factura Negociable Electronica", "Descuento de Factura Negociable Electronica",
                         "Descuento de Letra de Corto Plazo" -> {
                        this.pagoVigenteRepository.deletePagoVigentePorProducto(normalizados.getLast().producto());
                    }
                }
            }
            case "BBVA", "SCOTIA", "IBK" -> this.pagoVigenteRepository.deletePagoVigentePorCodigo(codigoBanco);
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
                    e.setEstadoRegistro(true);
                    e.setProducto(dto.producto());
                    return e;
                })
                .toList();

        pagoVigenteRepository.saveAll(entities);
        log.info("Se registraron {} pagos vigentes del banco {}", entities.size(), codigoBanco);
    }

    private BancoParser<PagoVigenteNormalizadoDto> seleccionarParser(String codigoBanco) {
        return switch (codigoBanco.toUpperCase()) {
            case "BBVA" -> bbvaVigenteParser;
            case "SCOTIA", "SCOTIABANK" -> scotiabankVigenteParser;
            case "BCP" -> bcpVigenteParser;
            case "IBK" -> interbankVigenteParser;
            default -> throw new RuntimeException("Parser de vigentes no implementado para: " + codigoBanco);
        };
    }

    private static String removerAcento(String texto){
        if(texto == null) return null;
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return textoNormalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
