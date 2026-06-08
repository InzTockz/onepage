package com.battilana.onepage.controller;

import com.battilana.onepage.dto.borradores.ComentarioPedidoRequest;
import com.battilana.onepage.dto.borradores.PedidoDiarioClientResponse;
import com.battilana.onepage.dto.borradores.PedidoDiarioResponse;
import com.battilana.onepage.service.BorradoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borradores")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BorradoresController {

    private final BorradoresService borradoresService;

    @GetMapping("/pedido-diario")
    public ResponseEntity<List<PedidoDiarioClientResponse>> buscarPedidoDiario(){
        return ResponseEntity.status(HttpStatus.OK).body(this.borradoresService.buscarPedidosDiarios());
    }

    @GetMapping("/lista/pedidos-diarios")
    public ResponseEntity<List<PedidoDiarioResponse>> listaPedidosDiarios(){
        return ResponseEntity.status(HttpStatus.OK).body(this.borradoresService.listaPedidosDiarios());
    }

    @PostMapping("/registro-pedidos")
    public ResponseEntity<Void> registroPedidosDiarios(){
        this.borradoresService.registroPedidosDiarios();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/pedidos-diarios/generar-lote")
    public ResponseEntity<Void> generarLotePedidosDiarios(@RequestBody List<ComentarioPedidoRequest> comentarios){
        this.borradoresService.generarLotePedidosDiarios(comentarios);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/pedidos-diarios/enviar-lote")
    public ResponseEntity<Void> enviarLotePedidosDiarios() {
        this.borradoresService.enviarLotePedidoDiarios();
        return ResponseEntity.ok().build();
    }
}
