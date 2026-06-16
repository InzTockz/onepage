package com.battilana.onepage.service;

import org.springframework.web.multipart.MultipartFile;

public interface PagoService {

    void registrarDocumentos(MultipartFile archivo, String codigoBanco);
}
