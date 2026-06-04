package com.battilana.onepage.service;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezClientResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraClientResponse;

import java.util.List;

public interface FacturaClienteClientService {

    List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrar();
    List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorCliente(String ruc);
    List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorVendedor(Integer slpCode);
    List<FacturasPorCobrarTopDiezClientResponse> buscarFacturasPorCobrarTopDiez();
    List<ResumenCarteraClientResponse> resumenCartera();
}
