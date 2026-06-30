package com.battilana.onepage.controller;

import com.battilana.onepage.service.PagoVigenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/pagos-vigentes")
@RequiredArgsConstructor
public class PagoVigenteController {

    private final PagoVigenteService pagoVigenteService;

    @PostMapping("/cargar")
    public ResponseEntity<Void> cargar(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("codigoBanco") String codigoBanco) {
        pagoVigenteService.registrarPagosVigentes(archivo, codigoBanco);
        return ResponseEntity.ok().build();
    }
}
