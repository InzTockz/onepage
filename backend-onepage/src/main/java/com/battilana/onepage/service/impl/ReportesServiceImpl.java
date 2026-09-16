package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.FacturaClienteClient;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.entity.BancoEntity;
import com.battilana.onepage.entity.PagoEntity;
import com.battilana.onepage.entity.PagoVigenteEntity;
import com.battilana.onepage.repository.PagoRepository;
import com.battilana.onepage.repository.PagoVigenteRepository;
import com.battilana.onepage.service.ReportesService;
import com.battilana.onepage.util.NumeroFacturaUtil;
import com.battilana.onepage.util.report.EstadoCuentaExcelBuilder;
import com.battilana.onepage.util.report.EstadoCuentaPdfBuilder;
import com.battilana.onepage.util.report.ResumenCarteraExcelBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements ReportesService {

    private final ResumenCarteraExcelBuilder resumenCarteraExcelBuilder;
    private final EstadoCuentaExcelBuilder estadoCuentaExcelBuilder;
    private final FacturaClienteClient facturaClienteClient;
    private final EstadoCuentaPdfBuilder estadoCuentaPdfBuilder;
    private final PagoRepository pagoRepository;
    private final PagoVigenteRepository pagoVigenteRepository;

    @Override
    public byte[] reporteGeneralDeFacturas() throws IOException {
        return resumenCarteraExcelBuilder.build(facturaClienteClient.buscarFacturasPorCobrar());
    }

    @Override
    public byte[] generarEstadoCuentaPorVendedor(Integer slpCode) throws IOException {
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode);
        return this.estadoCuentaExcelBuilder.build(facturasEnriquecidas(facturas));
    }

    @Override
    public byte[] reporteEstadoCuentaPorVendedorPdf(Integer slpCode) throws IOException {
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode);
        return this.estadoCuentaPdfBuilder.build(facturasEnriquecidas(facturas));
    }

    @Override
    public byte[] reporteFactuasYLetrasCanceladasCSV(Short idBanco) throws IOException {

        List<PagoEntity> pagoEntities = this.pagoRepository.buscarPorIdBanco(idBanco);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);

        switch (idBanco) {
            case 1 -> {
                /* FORMATO BBVA */
                CSVFormat formato = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setHeader("nro_transaccion", "nro_factura", "cliente", "operacion", "vencimiento",
                                "moneda", "importe", "estado")
                        .get();

                try (Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                        CSVPrinter printer = new CSVPrinter(writer, formato)) {
                    for (PagoEntity p : pagoEntities){
                        printer.printRecord(
                                p.getNroTransaccion(),
                                p.getNroFactura(),
                                p.getAceptante(),
                                p.getFechaOperacion(),
                                p.getFechaVencimiento(),
                                p.getMoneda(),
                                p.getImporte(),
                                p.getEstadoOriginal()
                        );
                    }
                    printer.flush();
                }
            }
            case 2 -> {
                /* FORMATO BCP */
                CSVFormat formato = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setHeader("nro_transaccion", "nro_factura", "cliente", "operacion", "vencimiento",
                                "moneda", "importe", "estado")
                        .get();
                try (Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                     CSVPrinter printer = new CSVPrinter(writer, formato)){
                    for (PagoEntity p : pagoEntities){
                        printer.printRecord(
                                p.getNroTransaccion(),
                                p.getNroFactura(),
                                p.getAceptante(),
                                p.getFechaOperacion(),
                                p.getFechaVencimiento(),
                                p.getMoneda(),
                                p.getImporte(),
                                p.getEstadoOriginal()
                        );
                    }
                    printer.flush();
                }
            }
            case 3 -> {
                /* FORMATO SCOTIABANK */
                CSVFormat formato = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setHeader("nro_transaccion", "nro_factura", "cliente", "operacion", "vencimiento",
                                "moneda", "importe", "estado")
                        .get();
                try (Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                CSVPrinter printer = new CSVPrinter(writer, formato)){
                    for(PagoEntity p : pagoEntities){
                        printer.printRecord(
                                p.getNroTransaccion(),
                                p.getNroFactura(),
                                p.getAceptante(),
                                p.getFechaOperacion(),
                                p.getFechaVencimiento(),
                                p.getMoneda(),
                                p.getImporte(),
                                p.getEstadoOriginal()
                        );
                    }
                    printer.flush();
                }
            }
            case 4 -> {
                /* FORMATO INTERBANK */
                CSVFormat formato = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setHeader("nro_transaccion", "nro_factura", "cliente", "operacion", "vencimiento",
                                "moneda", "importe", "estado")
                        .get();
                try(Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                CSVPrinter printer = new CSVPrinter(writer, formato)){
                    for(PagoEntity p : pagoEntities){
                        printer.printRecord(
                                p.getNroTransaccion(),
                                p.getNroFactura(),
                                p.getAceptante(),
                                p.getFechaOperacion(),
                                p.getFechaVencimiento(),
                                p.getMoneda(),
                                p.getImporte(),
                                p.getEstadoOriginal()
                        );
                    }
                }
            }
        }

        return baos.toByteArray();
    }

    private List<FacturasPorCobrarClientResponse> facturasEnriquecidas(List<FacturasPorCobrarClientResponse> facturas){

        List<PagoVigenteEntity> vigentes = pagoVigenteRepository.findAllWithBank();

        Map<String, PagoVigenteEntity> porFactura = vigentes.stream()
                .collect(Collectors.toMap(
                        v -> NumeroFacturaUtil.normalizar(v.getNroFactura()),
                        v -> v,
                        (a, b) -> a
                ));

        return facturas.stream()
                .map(f -> {
                    PagoVigenteEntity v = porFactura
                            .get(NumeroFacturaUtil.normalizar(f.comprobante()));
                    if( v == null) return f;
                    return f.conVinculacion(v.getNroUnico(), v.getBancoEntity().getCodigo());
                }).toList();
    }
}
