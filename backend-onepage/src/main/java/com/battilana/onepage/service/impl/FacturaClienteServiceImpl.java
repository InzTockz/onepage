package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraResponse;
import com.battilana.onepage.entity.FacturaClienteEntity;
import com.battilana.onepage.mappers.FacturaClienteMapper;
import com.battilana.onepage.repository.FacturaClienteRepository;
import com.battilana.onepage.service.FacturaClienteClientService;
import com.battilana.onepage.service.FacturaClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturaClienteServiceImpl implements FacturaClienteService {

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
    public void registrarFacturasDelMes() {
        log.info("Iniciando registro automatico de facturas");

        Integer anioActual = LocalDate.now().getYear();

        //1. Calcular el siguiente periodo
        Integer ultimoPeriodo = facturaClienteRepository.obtenerUltimoPeriodo(anioActual);
        int nuevoPeriodo = ultimoPeriodo == null ? 1 : ultimoPeriodo + 1;

        //2. Validar que no exceda los 12 meses
        if(nuevoPeriodo > 12){
            log.warn("Ya se registraron los 12 periodos del año. No se realizara el registro.");
            return;
        }

        //3. Obtener facturas desde la API externa
        List<FacturasPorCobrarClientResponse> facturas = facturaClienteClientService.buscarFacturasPorCobrar();

        if(facturas.isEmpty()){
            log.warn("No se encontraron facturas para registrar.");
            return;
        }

        //4. Convertir y asignar el nuevo periodo
        List<FacturaClienteEntity> entities = facturas.stream()
                .map(f -> {
                    FacturaClienteEntity entity = new FacturaClienteEntity();
                    entity.setRuc(f.ruc());
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
                    entity.setPeriodo(nuevoPeriodo);
                    return entity;
                }).toList();

        facturaClienteRepository.saveAll(entities);
        log.info("Se registraron {} facturas para el periodo {}", entities.size(), nuevoPeriodo);
    }

    @Override
    public void registrarFacturasDelMesManual() {

    }
}
