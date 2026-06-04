package com.battilana.onepage.service;

import com.battilana.onepage.dto.borradores.LotePedidosResponse;

import java.util.List;

public interface LotePedidoService {

    void registrar();
    List<LotePedidosResponse> listar();
    void generarEnvios();
}
