package com.battilana.onepage.controller;

import com.battilana.onepage.dto.vendedor.VendedorResponse;
import com.battilana.onepage.service.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendedor")
@CrossOrigin("*")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService vendedorService;

    @GetMapping()
    public ResponseEntity<List<VendedorResponse>> listarVendedores(){
        return ResponseEntity.status(HttpStatus.OK).body(this.vendedorService.listarVendedores());
    }
}
