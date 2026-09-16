package com.battilana.onepage.dto.borradores;

public record DetalleBorradoresResponse (
        Integer lineNum,
        String itemCode,
        String description,
        Double quantity,
        Double price,
        Integer docEntry
){
}
