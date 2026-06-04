package com.battilana.onepage.controller;

import com.battilana.onepage.dto.borradores.LotePedidosResponse;
import com.battilana.onepage.service.LotePedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lote-pedido")
@CrossOrigin("*")
@RequiredArgsConstructor
public class LotePedidoController {

    private final LotePedidoService lotePedidoService;

    @GetMapping("/listar")
    public ResponseEntity<List<LotePedidosResponse>> listar(){
        return ResponseEntity.status(HttpStatus.OK).body(this.lotePedidoService.listar());
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(){
        this.lotePedidoService.registrar();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/generar-envio")
    public ResponseEntity<Void> generarEnvios(){
        this.lotePedidoService.generarEnvios();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
