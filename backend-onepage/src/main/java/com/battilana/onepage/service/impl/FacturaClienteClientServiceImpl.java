package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.FacturaClienteClient;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezMasVencidasClientResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraClientResponse;
import com.battilana.onepage.entity.PagoVigenteEntity;
import com.battilana.onepage.repository.PagoVigenteRepository;
import com.battilana.onepage.service.FacturaClienteClientService;
import com.battilana.onepage.util.NumeroFacturaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaClienteClientServiceImpl implements FacturaClienteClientService {

    private final FacturaClienteClient facturaClienteClient;
    private final PagoVigenteRepository pagoVigenteRepository;

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrar() {
//        return this.facturaClienteClient.buscarFacturasPorCobrar();
        return facturasEnriquecidas(this.facturaClienteClient.buscarFacturasPorCobrar());
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

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorCliente(String ruc) {
        return facturasEnriquecidas(this.facturaClienteClient.buscarFacturasPorCobrarPorCliente(ruc));
    }

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorCobrarPorVendedor(Integer slpCode) {
        return facturasEnriquecidas(this.facturaClienteClient.buscarFacturasPorCobrarPorVendedor(slpCode));
    }

    @Override
    public List<FacturasPorCobrarClientResponse> buscarFacturasPorVendedorYCliente(Integer slpCode, String ruc) {
        return facturasEnriquecidas(this.facturaClienteClient.buscarFacturasPorVendedorYCliente(slpCode, ruc));
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
