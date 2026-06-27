package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.FacturaClienteClient;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.service.ReportesService;
import com.battilana.onepage.util.report.ExcelReportBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements ReportesService {

    private final ExcelReportBuilder excelReportBuilder;
    private final FacturaClienteClient facturaClienteClient;

    @Override
    public byte[] reporteGeneralDeFacturas() throws IOException {
        return excelReportBuilder.build(facturaClienteClient.buscarFacturasPorCobrar());
    }

    @Override
    public byte[] generarExcel(Integer slpCode) throws IOException {
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode);
        return excelReportBuilder.build(facturas);
    }

    @Override
    public byte[] generarPdf(List<FacturasPorCobrarResponse> facturas) throws IOException {
        return new byte[0];
    }
}
