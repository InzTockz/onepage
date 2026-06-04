package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.VendedorClient;
import com.battilana.onepage.dto.vendedor.VendedorResponse;
import com.battilana.onepage.service.VendedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendedorServiceImpl implements VendedorService {

    private final VendedorClient vendedorClient;

    @Override
    public List<VendedorResponse> listarVendedores() {
        return this.vendedorClient.listaVendedores();
    }
}
