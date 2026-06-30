package com.battilana.onepage.service;

import org.springframework.web.multipart.MultipartFile;

public interface PagoVigenteService {

    void registrarPagosVigentes(MultipartFile archivo, String codigoBanco);
}
