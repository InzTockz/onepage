package com.battilana.onepage.service;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;

import java.io.IOException;
import java.util.List;

public interface ReportesService {

    byte[] reporteGeneralDeFacturas() throws  IOException;
    byte[] generarEstadoCuentaPorVendedor(Integer slpCode) throws IOException;
    byte[] reporteEstadoCuentaPorVendedorPdf(Integer slpCode) throws IOException;
    byte[] reporteFactuasYLetrasCanceladasCSV(Short idBanco ) throws IOException;
}
