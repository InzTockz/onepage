package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.FacturaClienteClient;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.service.ReportesService;
import com.battilana.onepage.util.report.EstadoCuentaExcelBuilder;
import com.battilana.onepage.util.report.EstadoCuentaPdfBuilder;
import com.battilana.onepage.util.report.ResumenCarteraExcelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements ReportesService {

    private final ResumenCarteraExcelBuilder resumenCarteraExcelBuilder;
    private final EstadoCuentaExcelBuilder estadoCuentaExcelBuilder;
    private final FacturaClienteClient facturaClienteClient;
    private final EstadoCuentaPdfBuilder estadoCuentaPdfBuilder;

    @Override
    public byte[] reporteGeneralDeFacturas() throws IOException {
        return resumenCarteraExcelBuilder.build(facturaClienteClient.buscarFacturasPorCobrar());
    }

    @Override
    public byte[] generarEstadoCuentaPorVendedor(Integer slpCode) throws IOException {
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode);
        return this.estadoCuentaExcelBuilder.build(facturas);
    }

    @Override
    public byte[] reporteEstadoCuentaPorVendedorPdf(Integer slpCode) throws IOException {
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode);
        return this.estadoCuentaPdfBuilder.build(facturas);
    }
}
