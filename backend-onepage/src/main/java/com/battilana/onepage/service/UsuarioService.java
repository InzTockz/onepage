package com.battilana.onepage.service;

import com.battilana.onepage.dto.usuario.UsuarioRequest;
import com.battilana.onepage.dto.usuario.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listarUsuarios();
    UsuarioResponse buscarUsuario(Integer idUsuario);
    UsuarioResponse registrarUsuario(UsuarioRequest usuarioRequest);
    UsuarioResponse actualizarUsuario(Integer idUsuario, UsuarioRequest usuarioRequest);
    void eliminarUsuario();
}
