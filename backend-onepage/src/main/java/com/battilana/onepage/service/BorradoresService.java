package com.battilana.onepage.service;

import com.battilana.onepage.dto.borradores.ComentarioPedidoRequest;
import com.battilana.onepage.dto.borradores.PedidoDiarioClientResponse;
import com.battilana.onepage.dto.borradores.PedidoDiarioResponse;

import java.util.List;

public interface BorradoresService {

    List<PedidoDiarioClientResponse> buscarPedidosDiarios();

    //GESTION BASE DE DATOS LOCAL
    List<PedidoDiarioResponse> listaPedidosDiarios();
    void registroPedidosDiarios();
    void generarLotePedidosDiarios(List<ComentarioPedidoRequest> comentarioPedidoRequests);
    void enviarLotePedidoDiarios();
}
