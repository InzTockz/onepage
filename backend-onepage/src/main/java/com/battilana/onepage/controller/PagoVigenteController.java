package com.battilana.onepage.controller;

import com.battilana.onepage.dto.pago.PagoVigenteResponse;
import com.battilana.onepage.service.PagoVigenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos-vigentes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PagoVigenteController {

    private final PagoVigenteService pagoVigenteService;

    @GetMapping("/listado")
    public ResponseEntity<List<PagoVigenteResponse>> listado(){
        return ResponseEntity.ok(this.pagoVigenteService.listado());
    }

    @PostMapping("/cargar")
    public ResponseEntity<Void> cargar(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("codigoBanco") String codigoBanco) {
        pagoVigenteService.registrarPagosVigentes(archivo, codigoBanco);
        return ResponseEntity.ok().build();
    }
}
