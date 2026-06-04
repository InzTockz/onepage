package com.battilana.onepage.service;

import com.battilana.onepage.dto.borradores.PedidoDiarioResponse;

import java.util.List;

public interface BorradoresService {

    List<PedidoDiarioResponse> buscarPedidosDiarios();
}
