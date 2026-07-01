package com.battilana.onepage.service;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import com.battilana.onepage.dto.pago.PagoVigenteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PagoVigenteService {

    List<PagoVigenteResponse> listado();
    void registrarPagosVigentes(MultipartFile archivo, String codigoBanco);
}
