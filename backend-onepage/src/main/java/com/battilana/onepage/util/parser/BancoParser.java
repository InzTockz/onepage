package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface BancoParser <T> {
    boolean coincideFormato(Workbook workbook); // huella del formato

//    List<PagoNormalizadoDto> parsear(Workbook workbook);
    List<T> parsear(Workbook workbook);
}
