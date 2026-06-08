package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.BorradoresClient;
import com.battilana.onepage.dto.borradores.ComentarioPedidoRequest;
import com.battilana.onepage.dto.borradores.PedidoDiarioClientResponse;
import com.battilana.onepage.dto.borradores.PedidoDiarioResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.entity.BorradoresEntity;
import com.battilana.onepage.enums.EstadoBorrador;
import com.battilana.onepage.mappers.BorradoresMapper;
import com.battilana.onepage.repository.BorradoresRepository;
import com.battilana.onepage.service.BorradoresService;
import com.battilana.onepage.service.FacturaClienteClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BorradoresServiceImpl implements BorradoresService {

    private final BorradoresClient borradoresClient;
    private final BorradoresRepository borradoresRepository;
    private final BorradoresMapper borradoresMapper;
    private final FacturaClienteClientService facturaClienteClientService;

    @Override
    public List<PedidoDiarioClientResponse> buscarPedidosDiarios() {
        return this.borradoresClient.buscarPedidosDiarios();
    }

    @Override
    public List<PedidoDiarioResponse> listaPedidosDiarios() {
        return this.borradoresMapper.toListPedidodiarioResponse(this.borradoresRepository.findByEstadoBorrador());
    }

    @Override
    public void registroPedidosDiarios() {
        List<PedidoDiarioClientResponse> pedidoDiario = this.borradoresClient.buscarPedidosDiarios();
        List<BorradoresEntity> pedidoDiarioEntity = new ArrayList<>();

        for (PedidoDiarioClientResponse p : pedidoDiario) {
            BorradoresEntity pd = this.borradoresRepository.findByDocEntry(p.docEntry());
            if (pd == null) {
                pd = new BorradoresEntity();
                pd.setDocEntry(p.docEntry());
                pd.setCodCliente(p.cardCode());
                pd.setNombre(p.cardName());
                pd.setEstadoBorrador(EstadoBorrador.PEDIDO_REGISTRADO);
                pd.setComentario("");
                actualizarCampos(pd, p);
                pedidoDiarioEntity.add(pd);
            } else if(pd.getEstadoBorrador() == EstadoBorrador.PEDIDO_REGISTRADO) {
                actualizarCampos(pd, p);
                pedidoDiarioEntity.add(pd);
            }
        }

        this.borradoresRepository.saveAll(pedidoDiarioEntity);

        log.info("Sincronizacion completada. Procesados {}", pedidoDiarioEntity.size());
    }

    @Override
    public void generarLotePedidosDiarios(List<ComentarioPedidoRequest> comentarioPedidoRequests) {
        List<BorradoresEntity> actualizar = new ArrayList<>();

        for (ComentarioPedidoRequest c : comentarioPedidoRequests){
            BorradoresEntity borradores = borradoresRepository.findByDocEntry(c.docEntry());
            List<FacturasPorCobrarClientResponse> listadoFacturas = this.facturaClienteClientService.buscarFacturasPorCobrarPorCliente(c.codCliente());
            if(borradores != null){
                borradores.setComentario(c.comentario());
                borradores.setEstadoBorrador(EstadoBorrador.LOTE_GENERADO);
                LocalDate hoy = LocalDate.now();
                String facturas = listadoFacturas.stream()
                        .filter(f -> {
                            LocalDate vencimiento = LocalDate.parse(f.vencimiento());
                            return vencimiento.isBefore(hoy);
                        }).map(FacturasPorCobrarClientResponse::comprobante).collect(Collectors.joining(" | "));
                actualizar.add(borradores);
                borradores.setFacturasVencidas(facturas);
            }
        }
        borradoresRepository.saveAll(actualizar);
        log.info("Comentario actualizados: {}", actualizar.size());
    }

    @Override
    public void enviarLotePedidoDiarios() {
        List<BorradoresEntity> lotesGenerados = borradoresRepository.findByEstadoBorradorLoteGenerado();

        lotesGenerados.forEach(b -> {
            b.setEstadoBorrador(EstadoBorrador.LOTE_ENVIADO);
            b.setFechaLoteEnviado(LocalDateTime.now());
        });

        borradoresRepository.saveAll(lotesGenerados);
        log.info("Lote enviado con {} pedidos", lotesGenerados.size());
    }

    private void actualizarCampos(BorradoresEntity pd, PedidoDiarioClientResponse p) {
        pd.setFechaCreacionPedido(p.fechaCreacionPedido());
        pd.setFechaRegistro(LocalDateTime.now());
        pd.setDocTime(p.docTime());
        pd.setMontoTotalPedido(p.docTotalFC());
        pd.setCondicionPago(p.pymntGroup());
        pd.setFechaUltimaFacturaPagada(p.docDate());

        if (p.creditLine().compareTo(BigDecimal.ZERO) > 0
                && !p.pymntGroup().equalsIgnoreCase("Contado")) {
            BigDecimal deudaTotal = p.montoPorVencer().add(p.montoVencido());
            BigDecimal lineaUtilizada = deudaTotal
                    .divide(p.creditLine(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            pd.setLineaCredito(p.creditLine());
            pd.setMontoPorCobrar(p.montoPorVencer());
            pd.setMontoVencido(p.montoVencido());
            pd.setLineaCreditoUtilizada(lineaUtilizada);
            pd.setMontoTotalDeuda(deudaTotal);
            pd.setMora(BigDecimal.valueOf(100));
            pd.setNroFacturasVencidas(p.facturasVencidas());
            pd.setFechaFacturaVencidaMasAntigua(p.fechaVencida());
        } else {
            pd.setLineaCredito(BigDecimal.ZERO);
            pd.setMontoPorCobrar(BigDecimal.ZERO);
            pd.setMontoVencido(BigDecimal.ZERO);
            pd.setLineaCreditoUtilizada(BigDecimal.ZERO);
            pd.setMontoTotalDeuda(BigDecimal.ZERO);
            pd.setMora(BigDecimal.ZERO);
            pd.setNroFacturasVencidas(0L);
            pd.setFechaFacturaVencidaMasAntigua(
                    LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.of(0, 0)));
        }
    }
}
