package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.borradores.LotePedidosResponse;
import com.battilana.onepage.dto.borradores.PedidoDiarioClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.entity.LotePedidosEntity;
import com.battilana.onepage.mappers.LotePedidoMapper;
import com.battilana.onepage.repository.LotePedidosRepository;
import com.battilana.onepage.service.BorradoresService;
import com.battilana.onepage.service.FacturaClienteClientService;
import com.battilana.onepage.service.LotePedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LotePedidoServiceImpl implements LotePedidoService {

    private final LotePedidosRepository lotePedidosRepository;
    private final BorradoresService borradoresService;
    private final LotePedidoMapper lotePedidoMapper;
    private final FacturaClienteClientService facturaClienteClientService;

    @Override
    public void registrar() {
        List<PedidoDiarioClientResponse> pedidoDiarioClientResponse = this.borradoresService.buscarPedidosDiarios();
        List<LotePedidosEntity> lotePedidoEntities = new ArrayList<>();

        if (pedidoDiarioClientResponse != null) {
            for (PedidoDiarioClientResponse pd : pedidoDiarioClientResponse) {
                List<FacturasPorCobrarClientResponse> listadoFacturas = this.facturaClienteClientService.buscarFacturasPorCobrarPorCliente(pd.cardCode());
                LotePedidosEntity lp = new LotePedidosEntity();
                lp.setCodCliente(pd.cardCode());
                lp.setNombre(pd.cardName());
                lp.setFechaCreacion(LocalDate.now());
                lp.setFechaRecorte(LocalDateTime.now());
                lp.setMontoTotal(pd.docTotalFC());
                lp.setCondicionPago(pd.pymntGroup());
                if(pd.creditLine().compareTo(BigDecimal.ZERO) > 0  && !pd.pymntGroup().equalsIgnoreCase("Contado")){
                    lp.setLineaCredito(pd.creditLine());
                    lp.setMontoPorCobrar(pd.montoPorVencer());
                    lp.setMontoVencido(pd.montoVencido());
                    lp.setLineaCreditoUtilizada(
                            pd.montoPorVencer().add(pd.montoVencido()).divide(pd.creditLine(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    );
                    lp.setMora(BigDecimal.valueOf(100));
                    lp.setNroFacturasVencidas(pd.facturasVencidas());
                    lp.setFechaFacturaVencidaMasAntigua(pd.fechaVencida());
                } else {
                    lp.setLineaCredito(BigDecimal.ZERO);
                    lp.setMontoPorCobrar(BigDecimal.ZERO);
                    lp.setMontoVencido(BigDecimal.ZERO);
                    lp.setLineaCreditoUtilizada(BigDecimal.ZERO);
                    lp.setMora(BigDecimal.ZERO);
                    lp.setNroFacturasVencidas(0L);
                    lp.setFechaFacturaVencidaMasAntigua(LocalDateTime.of(LocalDate.of(1900, 1,1), LocalTime.of(0, 0)));
                }
                lp.setEstado(true);
                lp.setFechaUltimaFacturaPagada(pd.docDate());
                LocalDate hoy = LocalDate.now();
                String facturas = listadoFacturas.stream()
                        .filter(f -> {
                            LocalDate vencimiento = LocalDate.parse(f.vencimiento());
                            return vencimiento.isBefore(hoy);
                        }).map(FacturasPorCobrarClientResponse::comprobante).collect(Collectors.joining(" | "));

                lp.setFacturasVencidas(facturas.isEmpty() ? "0": facturas);
                lotePedidoEntities.add(lp);
            }
        }
        this.lotePedidosRepository.saveAll(lotePedidoEntities);
    }

    @Override
    public List<LotePedidosResponse> listar() {
        return this.lotePedidoMapper.toListResponse(this.lotePedidosRepository.findByEstadoTrue());
    }

    @Override
    public void generarEnvios() {
        List<LotePedidosEntity> listaLotePedidos = this.lotePedidosRepository.findByEstadoTrue();
        listaLotePedidos.forEach(lt -> lt.setEstado(false));

        lotePedidosRepository.saveAll(listaLotePedidos);
    }
}
