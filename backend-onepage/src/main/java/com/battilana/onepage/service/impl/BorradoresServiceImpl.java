package com.battilana.onepage.service.impl;

import com.battilana.onepage.client.BorradoresClient;
import com.battilana.onepage.dto.borradores.PedidoDiarioResponse;
import com.battilana.onepage.service.BorradoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorradoresServiceImpl implements BorradoresService {

    private final BorradoresClient borradoresClient;

    @Override
    public List<PedidoDiarioResponse> buscarPedidosDiarios() {
        return this.borradoresClient.buscarPedidosDiarios();
    }
}
