package com.battilana.onepage.service;

import com.battilana.onepage.dto.borradores.PedidoDiarioClientResponse;
import com.battilana.onepage.dto.borradores.PedidodiarioResponse;

import java.util.List;

public interface BorradoresService {

    List<PedidoDiarioClientResponse> buscarPedidosDiarios();

    //GESTION BASE DE DATOS LOCAL
    List<PedidodiarioResponse> listaPedidosDiarios();
    void registroPedidosDiarios();
}
