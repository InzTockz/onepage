package com.battilana.onepage.controller;

import com.battilana.onepage.dto.borradores.PedidoDiarioClientResponse;
import com.battilana.onepage.service.BorradoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
