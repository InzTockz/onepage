package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BbvaVigenteParser implements BancoParser<PagoVigenteNormalizadoDto> {

    /**
     * 1: F. Vencimiento | 2: No. Banco ] 3: No. Original |
     * 4: Aceptante | 5: Importe | 6: Situación |
     */

    @Override
    public boolean coincideFormato(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        return CeldaUtil.existeFilaConTokens(hoja, 25, "F. Vencimiento")
                && CeldaUtil.existeFilaConTokens(hoja, 25, "No. Original");
    }

    @Override
    public List<PagoVigenteNormalizadoDto> parsear(Workbook workbook) {

        Sheet hoja = workbook.getSheetAt(0);
        List<PagoVigenteNormalizadoDto> resultado = new ArrayList<>();

        int filaEncabezado = buscarFilaEncabezado(hoja);

        for(int i = filaEncabezado + 1; i <= hoja.getLastRowNum(); i++ ){
            Row fila = hoja.getRow(i);
            if(fila == null) continue;

            String primeraCelda = CeldaUtil.leerTexto(fila, 0);
            if(primeraCelda.contains("Situación") || primeraCelda.contains("Situacion")){
                break;
            }

            String nroBanco = CeldaUtil.leerTexto(fila, 1);
            if(nroBanco.isEmpty() || !nroBanco.matches("\\d+")) continue;

            String aceptante = CeldaUtil.leerTexto(fila, 3);
            if(aceptante.isEmpty()) continue;

            PagoVigenteNormalizadoDto dto = new PagoVigenteNormalizadoDto(
                    CeldaUtil.leerTexto(fila, 1),
                    CeldaUtil.leerTexto(fila, 2),
                    CeldaUtil.leerTexto(fila, 3),
                    null,
                    CeldaUtil.leerFechaExcel(fila, 0),
                    "Dolares",
                    CeldaUtil.leerDecimal(fila, 4),
                    CeldaUtil.leerTexto(fila, 5),
                    ""
            );

            resultado.add(dto);
        }
        return resultado;
    }

    private int buscarFilaEncabezado(Sheet hoja) {
        for (int i = 0; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            Cell celda = fila.getCell(0);
            if (celda == null || celda.getCellType() != CellType.STRING) continue;

            String texto = celda.getStringCellValue().trim();
            if (texto.contains("F. Vencimiento")) {
                return i;
            }
        }
        return -1;
    }
}
