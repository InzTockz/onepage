package com.battilana.onepage.service;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraResponse;

import java.util.List;

public interface FacturaClienteService {

    List<FacturasPorCobrarResponse> buscarFacturasPorAnioYPeriodo(Integer anio, Integer periodo);
    List<FacturasPorCobrarResponse> buscarFacturasPorAnio(Integer anio);
    List<ResumenCarteraResponse> resumenCartera();
    List<ResumenCarteraResponse> resumenCarteraPorPeriodo(Integer periodo);
    List<ResumenCarteraResponse> resumenCarteraPorPeriodoYVendedor(Integer periodo, String vendedor);
    List<ResumenCarteraResponse> resumenCarteraPorVendedor(String vendedor);
    void registrarFacturasDelMes();
    void registrarFacturasDelMesManual();

}
