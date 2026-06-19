package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraResponse;
import com.battilana.onepage.entity.FacturaClienteEntity;
import com.battilana.onepage.mappers.FacturaClienteMapper;
import com.battilana.onepage.repository.FacturaClienteRepository;
import com.battilana.onepage.service.FacturaClienteClientService;
import com.battilana.onepage.service.FacturaClienteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public
class FacturaClienteServiceImpl implements FacturaClienteService {

    private final FacturaClienteRepository facturaClienteRepository;
    private final FacturaClienteMapper facturaClienteMapper;
    private final FacturaClienteClientService facturaClienteClientService;

    @Override
    public List<FacturasPorCobrarResponse> buscarFacturasPorAnioYPeriodo(Integer anio, Integer periodo) {
        return this.facturaClienteMapper.toListDto(this.facturaClienteRepository.buscarFacturasPorAnioYPeriodo(anio, periodo));
    }

    @Override
    public List<FacturasPorCobrarResponse> buscarFacturasPorAnio(Integer anio) {
        return this.facturaClienteMapper.toListDto(this.facturaClienteRepository.buscarFacturasPorAnio(anio));
    }

    @Override
    public List<ResumenCarteraResponse> resumenCartera() {
        return this.facturaClienteRepository.resumenCartera();
    }

    @Override
    public List<ResumenCarteraResponse> resumenCarteraPorPeriodo(Integer periodo) {
        return this.facturaClienteRepository.resumenCarteraPorPeriodo(periodo);
    }

    @Override
    public List<ResumenCarteraResponse> resumenCarteraPorPeriodoYVendedor(Integer periodo, String vendedor) {
        return this.facturaClienteRepository.resumenCarteraPorPeriodoYVendedor(periodo, vendedor);
    }

    @Override
    public List<ResumenCarteraResponse> resumenCarteraPorVendedor(String vendedor) {
        return this.facturaClienteRepository.resumenCarteraPorVendedor(vendedor);
    }

    @Override
    @Transactional
    public void registrarFacturasDelMes(int periodo) {
        log.info("Iniciando registro automatico de facturas");

        //int mesSeleccionado = periodo;
        int anioActual = LocalDate.now().getYear();

        // 1. Validación: no permitir periodos futuros del año actual
        LocalDate hoy = LocalDate.now();
        LocalDate fechaSeleccionada = LocalDate.of(anioActual, periodo, 1);
        if (fechaSeleccionada.isAfter(hoy)) {
            log.warn("No se puede registrar un periodo futuro: {}", periodo);
            throw new RuntimeException("No se puede registrar un periodo que aun no ha ocurrido");
        }

        // 3. Calcular el ultimo dia del mes seleccionado
        LocalDate ultimoDiaDelMes = fechaSeleccionada.withDayOfMonth(fechaSeleccionada.lengthOfMonth());

        // 4. Obtener facturas desde la API
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClientService.buscarFacturasPorCobrar();

        if (facturas.isEmpty()) {
            log.warn("No se encontraron facturas para registrar.");
            return;
        }

        //4. Convertir y asignar el nuevo periodo
        List<FacturaClienteEntity> entities = facturas.stream()
                .map(f -> {
                    FacturaClienteEntity entity = this.facturaClienteRepository.buscarFacturaPorComprobantePorPeriodoPorAnio(f.comprobante(), periodo, anioActual);
                    if (entity != null) {
                        entity.setNombre(f.nombre());
                        entity.setDocumento(f.documento());
                        entity.setComprobante(f.comprobante());
                        entity.setEmision(f.emision());
                        entity.setVencimiento(f.vencimiento());
                        entity.setMoneda(f.moneda());
                        entity.setImporte(f.importe());
                        entity.setSaldo(f.saldo());
                        entity.setVendedor(f.vendedor());
                        entity.setLc(f.lc());
                        return entity;
                    } else {
                        FacturaClienteEntity entidadNueva = new FacturaClienteEntity();
                        entidadNueva.setRuc(f.ruc());
                        entidadNueva.setNombre(f.nombre());
                        entidadNueva.setDocumento(f.documento());
                        entidadNueva.setComprobante(f.comprobante());
                        entidadNueva.setEmision(f.emision());
                        entidadNueva.setVencimiento(f.vencimiento());
                        entidadNueva.setMoneda(f.moneda());
                        entidadNueva.setImporte(f.importe());
                        entidadNueva.setSaldo(f.saldo());
                        entidadNueva.setVendedor(f.vendedor());
                        entidadNueva.setLc(f.lc());
                        entidadNueva.setPeriodo(periodo);
                        entidadNueva.setFechaRegistro(ultimoDiaDelMes);
                        return entidadNueva;
                    }
                }).toList();

        facturaClienteRepository.saveAll(entities);
        log.info("Se registraron {} facturas para el periodo {}", entities.size(), ultimoDiaDelMes);
        log.info("Se registraron {} facturas para el periodo {}", entities.size(), periodo);
    }
}
