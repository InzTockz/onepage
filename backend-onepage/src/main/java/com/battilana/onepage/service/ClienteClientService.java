package com.battilana.onepage.service;

import com.battilana.onepage.dto.cliente.ClienteClientResponse;
import com.battilana.onepage.dto.cliente.ClienteDeudorClientResponse;

import java.util.List;

public interface ClienteClientService {

    List<ClienteClientResponse> listarClientes();
    List<ClienteClientResponse> listarClientesPorIdVendedor(Integer idVendedor);
    List<ClienteDeudorClientResponse> buscarClientesDeudores();
    List<ClienteDeudorClientResponse> buscarClientesDeudoresPorVendedor(Integer idVendedor);
}
