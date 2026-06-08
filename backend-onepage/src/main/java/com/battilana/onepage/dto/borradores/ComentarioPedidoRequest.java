package com.battilana.onepage.dto.borradores;

public record ComentarioPedidoRequest(
        Integer docEntry,
        String codCliente,
        String comentario
) {
}
