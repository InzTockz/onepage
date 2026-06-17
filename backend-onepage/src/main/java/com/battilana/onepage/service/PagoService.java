package com.battilana.onepage.service;

import com.battilana.onepage.dto.pago.PagoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PagoService {

    List<PagoResponse> listar();
    void registrarDocumentos(MultipartFile archivo, String codigoBanco);
}
