package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.BorradoresClient;
import com.battilana.onepage.dto.borradores.*;
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
import java.util.Objects;
import java.util.Optional;
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
    public List<BorradoresResponse> listaPedidosDiarios() {
        return this.borradoresMapper.toListPedidodiarioResponse(this.borradoresRepository.findByEstadoBorrador());
    }

    @Override
    public List<BorradoresResponse> listaPedidosGenerados() {
        return this.borradoresMapper.toListPedidodiarioResponse(this.borradoresRepository.findByEstadoBorradorLoteGenerado());
    }

    @Override
    public List<BorradoresResponse> listaPedidosEnviados() {
        return this.borradoresMapper.toListPedidodiarioResponse(this.borradoresRepository.findByEstadoBorradorEnviado());
    }

    @Override
    public void registroPedidosDiarios() {
        List<PedidoDiarioClientResponse> pedidoDiario = this.borradoresClient.buscarPedidosDiarios();
        List<BorradoresEntity> aRegistrar = new ArrayList<>();

        // PASE 1 — importar (O/W): insertar nuevos / actualizar existentes
        for (PedidoDiarioClientResponse p : pedidoDiario) {
            BorradoresEntity b = this.borradoresRepository.findByDocEntry(p.docEntry());
            if (b == null) {
                BorradoresEntity bNew = new BorradoresEntity();
                bNew.setDocEntry(p.docEntry());
                bNew.setCodCliente(p.cardCode());
                bNew.setNombre(p.cardName());
                bNew.setEstadoBorrador(EstadoBorrador.PEDIDO_REGISTRADO);
                bNew.setComentario("");
                bNew.setEstado(true);
                actualizarCampos(bNew, p);
                aRegistrar.add(bNew);
            } else {
                b.setEstadoBorrador(EstadoBorrador.PEDIDO_REGISTRADO);
                b.setEstado(true);
                actualizarCampos(b, p);
                aRegistrar.add(b);
            }
        }
        this.borradoresRepository.saveAll(aRegistrar);

        // PASE 2 — anular los registrados que en SAP estén O/C
        List<BorradoresEntity> aAnular = new ArrayList<>();
        for (BorradoresEntity pr : this.borradoresRepository.findByEstadoBorradorRegistrado()) {
            BorradoresClientResponse bc = this.borradoresClient.buscarBorradorPorDocEntry(pr.getDocEntry());
            if (bc != null && ("O".equalsIgnoreCase(bc.docStatus()) && "C".equalsIgnoreCase(bc.wddStatus()))) {
                pr.setEstadoBorrador(EstadoBorrador.ANULADO);
                pr.setEstado(false);
                aAnular.add(pr);
            } else if (bc != null && ("C".equalsIgnoreCase(bc.docStatus()) && "-".equalsIgnoreCase(bc.wddStatus()))){
                pr.setEstadoBorrador(EstadoBorrador.FACTURADO);
                pr.setEstado(false);
                aAnular.add(pr);
            } else if(bc != null && ("O".equalsIgnoreCase(bc.docStatus()) && "Y".equalsIgnoreCase(bc.wddStatus()))){
                pr.setEstadoBorrador(EstadoBorrador.AUTORIZADO);
                pr.setEstado(false);
                aAnular.add(pr);
            }
        }
        log.info("La lista de los pedidosDiarioEntity son: {}", pedidoDiario.size());
        this.borradoresRepository.saveAll(aAnular);
    }

    @Override
    public void generarLotePedidosDiarios(List<BorradoresRequest> borradoresRequests) {
        List<BorradoresEntity> actualizarBorradores = new ArrayList<>();
//        List<BorradoresEntity> borradores = this.borradoresRepository.findByEstadoBorradorRegistrado();

        for (BorradoresRequest b : borradoresRequests) {
            BorradoresEntity borradoresEntity = this.borradoresRepository.findByDocEntry(b.docEntry());

            List<FacturasPorCobrarClientResponse> listadoFacturas = this.facturaClienteClientService.buscarFacturasPorCobrarPorCliente(b.codCliente());
            borradoresEntity.setEstadoBorrador(EstadoBorrador.LOTE_GENERADO);
            borradoresEntity.setFechaLoteGenerado(LocalDateTime.now());
            LocalDate hoy = LocalDate.now();
            String facturas = listadoFacturas.stream()
                    .filter(f -> {
                        LocalDate vencimiento = LocalDate.parse(f.vencimiento());
                        return vencimiento.isBefore(hoy);
                    }).map(FacturasPorCobrarClientResponse::comprobante).collect(Collectors.joining(" | "));
            borradoresEntity.setFacturasVencidas(facturas);
            actualizarBorradores.add(borradoresEntity);
        }
        this.borradoresRepository.saveAll(actualizarBorradores);
        log.info("Comentario actualizados: {}", actualizarBorradores.size());
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

    @Override
    public BorradoresResponse agregarComentario(Integer idBorrador, BorradoresRequest borradoresRequest) {
        Optional<BorradoresEntity> borradoresEntity = this.borradoresRepository.findById(idBorrador);

        if (borradoresEntity.isPresent()) {
            borradoresEntity.get().setComentario(borradoresRequest.comentario());

            return this.borradoresMapper.toBorradoresResponse(this.borradoresRepository.save(borradoresEntity.get()));
        } else {
            return null;
        }
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
