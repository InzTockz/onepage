package com.battilana.onepage.service.impl;

import com.battilana.onepage.dto.usuario.UsuarioRequest;
import com.battilana.onepage.dto.usuario.UsuarioResponse;
import com.battilana.onepage.repository.UsuarioRepository;
import com.battilana.onepage.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return List.of();
    }

    @Override
    public UsuarioResponse buscarUsuario(Integer idUsuario) {
        return null;
    }

    @Override
    public UsuarioResponse registrarUsuario(UsuarioRequest usuarioRequest) {
        return null;
    }

    @Override
    public UsuarioResponse actualizarUsuario(Integer idUsuario, UsuarioRequest usuarioRequest) {
        return null;
    }

    @Override
    public void eliminarUsuario() {

    }
}
