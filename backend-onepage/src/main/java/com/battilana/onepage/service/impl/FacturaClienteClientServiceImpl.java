package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.FacturaClienteClient;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezMasVencidasClientResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraClientResponse;
import com.battilana.onepage.service.FacturaClienteClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaClienteClientServiceImpl implements FacturaClienteClientService {

    private final FacturaClienteClient facturaClienteClient;

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrar() {
        return this.facturaClienteClient.buscarFacturasPorCobrar();
    }

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorCliente(String ruc) {
        return this.facturaClienteClient.buscarFacturasPorCobrarPorCliente(ruc);
    }

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorVendedor(Integer slpCode) {
        return this.facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode);
    }

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorVendedorYCliente(Integer slpCode, String ruc) {
        return this.facturaClienteClient.buscarFacturasPorVendedorYCliente(slpCode, ruc);
    }

    @Override
    public List<FacturasPorCobrarTopDiezClientResponse> buscarFacturasPorCobrarTopDiez() {
        return this.facturaClienteClient.buscarFacturasPorCobrarTopDiez();
    }

    @Override
    public List<FacturasPorCobrarTopDiezMasVencidasClientResponse> facturasPorCobrarTopDiezMasVencidas() {
        return this.facturaClienteClient.facturasPorCobrarTopDiezMasVencidas();
    }

    @Override
    public List<ResumenCarteraClientResponse> resumenCartera() {
        return this.facturaClienteClient.resumentCartera();
    }
}
