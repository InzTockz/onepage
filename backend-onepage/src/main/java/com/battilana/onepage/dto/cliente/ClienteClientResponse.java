package com.battilana.onepage.dto.cliente;

public record ClienteClientResponse(
        String cardCode,
        String cardName,
        String email,
        String licTradNum,
        Double creditLine,
        Integer groupNum,
        String frozenFor,
        Integer listNum,
        Integer slpCode
) {
}
