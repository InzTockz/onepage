package com.battilana.onepage.controller;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraResponse;
import com.battilana.onepage.service.FacturaClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/factura")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FacturaClienteController {

    private final FacturaClienteService facturaClienteService;

    @GetMapping("/factura-cliente")
    public ResponseEntity<List<FacturasPorCobrarResponse>> buscarFacturasPorAnioYPeriodo(@RequestParam("anio") Integer anio, @RequestParam("periodo") Integer periodo) {
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteService.buscarFacturasPorAnioYPeriodo(anio, periodo));
    }

    @GetMapping("/facturas-por-cobrar/anio/{anio}")
    public ResponseEntity<List<FacturasPorCobrarResponse>> buscarFacturasPorAnio(@PathVariable Integer anio) {
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteService.buscarFacturasPorAnio(anio));
    }

    @GetMapping("/resumen-cartera")
    public ResponseEntity<List<ResumenCarteraResponse>> resumenCartera() {
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteService.resumenCartera());
    }

    @GetMapping("/resumen-cartera/periodo")
    public ResponseEntity<List<ResumenCarteraResponse>> resumenCarteraPorPeriodo(@RequestParam("periodo") Integer periodo) {
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteService.resumenCarteraPorPeriodo(periodo));
    }

    @GetMapping("/resumen-cartera/periodo-vendedor")
    public ResponseEntity<List<ResumenCarteraResponse>> resumenCarteraPorPeriodoYVendedor(@RequestParam("periodo") Integer periodo, @RequestParam("vendedor") String vendedor) {
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteService.resumenCarteraPorPeriodoYVendedor(periodo, vendedor));
    }

    @GetMapping("/resumen-cartera/vendedor")
    public ResponseEntity<List<ResumenCarteraResponse>> resumenCarteraPorVendedor(@RequestParam("vendedor") String vendedor) {
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteService.resumenCarteraPorVendedor(vendedor));
    }

    @PostMapping("/registro-manual")
    public ResponseEntity<Void> registrarFacturasDelMes() {
        this.facturaClienteService.registrarFacturasDelMes();
        return ResponseEntity.ok().build();
    }
}
