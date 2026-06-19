package com.battilana.onepage.service;

import com.battilana.onepage.dto.borradores.*;

import java.util.List;

public interface BorradoresService {

    List<PedidoDiarioClientResponse> buscarPedidosDiarios();

    //GESTION BASE DE DATOS LOCAL
    List<BorradoresResponse> listaPedidosDiarios();
    List<BorradoresResponse> listaPedidosGenerados();
    List<BorradoresResponse> listaPedidosEnviados();
    void registroPedidosDiarios();
    void generarLotePedidosDiarios(List<BorradoresRequest> borradoresRequests);
    void enviarLotePedidoDiarios();
    BorradoresResponse agregarComentario(Integer idBorrador, BorradoresRequest borradoresRequest);
}
