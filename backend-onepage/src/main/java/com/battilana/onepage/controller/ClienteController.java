package com.battilana.onepage.controller;

import com.battilana.onepage.dto.cliente.ClienteClientResponse;
import com.battilana.onepage.dto.cliente.ClienteDeudorClientResponse;
import com.battilana.onepage.service.ClienteClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteClientService clienteClientService;

    @GetMapping()
    public ResponseEntity<List<ClienteClientResponse>> listarClientes(){
        return ResponseEntity.status(HttpStatus.OK).body(this.clienteClientService.listarClientes());
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<ClienteClientResponse>> listarClientesPorIdVendedor(@PathVariable Integer idVendedor){
        return ResponseEntity.status(HttpStatus.OK).body(this.clienteClientService.listarClientesPorIdVendedor(idVendedor));
    }

    @GetMapping("/deudor")
    public ResponseEntity<List<ClienteDeudorClientResponse>> buscarClientesDeudores(){
        return ResponseEntity.status(HttpStatus.OK).body(this.clienteClientService.buscarClientesDeudores());
    }

    @GetMapping("/deudor/vendedor")
    public ResponseEntity<List<ClienteDeudorClientResponse>> buscarClientesDeudoresPorVendedor(@RequestParam("idVendedor") Integer idVendedor){
        return ResponseEntity.status(HttpStatus.OK).body(this.clienteClientService.buscarClientesDeudoresPorVendedor(idVendedor));
    }
}
