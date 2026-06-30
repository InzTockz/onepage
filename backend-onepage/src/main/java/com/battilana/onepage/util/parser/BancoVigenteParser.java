package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface BancoVigenteParser {

    boolean coincideFormato(Workbook workbook);
    List<PagoVigenteNormalizadoDto> parsear(Workbook workbook);
}
