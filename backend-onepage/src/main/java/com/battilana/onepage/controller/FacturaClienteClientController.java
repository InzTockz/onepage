package com.battilana.onepage.controller;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarTopDiezMasVencidasClientResponse;
import com.battilana.onepage.dto.facturas.ResumenCarteraClientResponse;
import com.battilana.onepage.service.FacturaClienteClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas-cliente")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FacturaClienteClientController {

    private final FacturaClienteClientService facturaClienteClientService;

    @GetMapping("/facturas-por-cobrar")
    public ResponseEntity<List<FacturasPorCobrarClientResponse>> buscarFacturasPorCobrar(){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.buscarFacturasPorCobrar());
    }

    @GetMapping("/facturas-por-cobrar/cliente/{ruc}")
    public ResponseEntity<List<FacturasPorCobrarClientResponse>> buscarFacturasPorCobrarPorCliente(@PathVariable String ruc){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.buscarFacturasPorCobrarPorCliente(ruc));
    }

    @GetMapping("/facturas-por-cobrar/vendedor/{slpCode}")
    public ResponseEntity<List<FacturasPorCobrarClientResponse>> buscarFacturasPorCobrarPorVendedor(@PathVariable Integer slpCode){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.buscarFacturasPorCobrarPorVendedor(slpCode));
    }

    @GetMapping("/facturas-por-cobrar/vendedor/{slpCode}/cliente/{ruc}")
    public ResponseEntity<List<FacturasPorCobrarClientResponse>> buscarFacturasPorVendedorYCliente(@PathVariable Integer slpCode, @PathVariable String ruc){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.buscarFacturasPorVendedorYCliente(slpCode, ruc));
    }

    @GetMapping("/facturas-por-cobrar/top-diez")
    public ResponseEntity<List<FacturasPorCobrarTopDiezClientResponse>> buscarFacturasPorCobrarTopDiez(){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.buscarFacturasPorCobrarTopDiez());
    }

    @GetMapping("/facturas-por-cobrar/vencidos/top-diez")
    public ResponseEntity<List<FacturasPorCobrarTopDiezMasVencidasClientResponse>> facturasPorCobrarTopDiezMasVencidas(){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.facturasPorCobrarTopDiezMasVencidas());
    }

    @GetMapping("/resumen-cartera")
    public ResponseEntity<List<ResumenCarteraClientResponse>> resumentCartera(){
        return ResponseEntity.status(HttpStatus.OK).body(this.facturaClienteClientService.resumenCartera());
    }
}
