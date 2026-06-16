package com.battilana.onepage.controller;

import com.battilana.onepage.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/bancos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BancoController {

    private final PagoService pagoService;

    @PostMapping("/cargar-excel")
    public ResponseEntity<Void> cargarExcel(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("banco") String codigoBanco) {
        pagoService.registrarDocumentos(archivo, codigoBanco);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
