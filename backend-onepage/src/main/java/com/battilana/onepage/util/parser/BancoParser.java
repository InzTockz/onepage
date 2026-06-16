package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface BancoParser {
    List<PagoNormalizadoDto> parsear(Workbook workbook);
}
