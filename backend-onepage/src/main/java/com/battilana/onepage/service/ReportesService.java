package com.battilana.onepage.service;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;

import java.io.IOException;
import java.util.List;

public interface ReportesService {

    byte[] reporteGeneralDeFacturas() throws  IOException;
    byte[] generarExcel(Integer slpCode) throws IOException;
    byte[] generarPdf(List<FacturasPorCobrarResponse> facturas) throws IOException;
}
