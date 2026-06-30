package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScotiabankVigenteParser implements BancoVigenteParser{

    // Estructura del archivo Scotiabank (.xls):
    //   filas 0-5 -> preámbulo (fecha de generación, RUC + empresa, línea resumen)
    //   fila  6   -> encabezados
    //   fila  7+  -> datos
    // Columnas:
    //   0: Nº. Banco             -> nroUnico (numérico)
    //   1: Nº. Proveedor/Girador -> nroFactura
    //   2: Pagador/Adquiriente   -> aceptante
    //   3: Fecha ingreso         -> texto dd/MM/yyyy
    //   4: Fecha vencimiento     -> texto dd/MM/yyyy
    //   5: Moneda                -> "$" (USD) / "S/" (PEN)
    //   6: Importe               -> numérico
    //   (no trae estado)

    @Override
    public boolean coincideFormato(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        int limite = Math.min(15, hoja.getLastRowNum());
        for (int i = 0; i <= limite; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            String texto = textoDeFila(fila);
            if (texto.contains("Banco") && texto.contains("Pagador/Adquiriente")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<PagoVigenteNormalizadoDto> parsear(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        List<PagoVigenteNormalizadoDto> resultado = new ArrayList<>();

        int filaEncabezado = buscarFilaEncabezado(hoja);
        if (filaEncabezado == -1) return resultado;

        for (int i = filaEncabezado + 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;

            // Fila de datos válida: el Nº. Banco (col 0) debe ser numérico
            String nroUnico = CeldaUtil.leerTexto(fila, 0);
            if (nroUnico.isEmpty() || !nroUnico.matches("\\d+")) continue;

            String nroFactura = CeldaUtil.leerTexto(fila, 1);
            if (nroFactura.isEmpty()) continue;

            resultado.add(new PagoVigenteNormalizadoDto(
                    nroUnico,
                    nroFactura,
                    CeldaUtil.leerTexto(fila, 2),
                    CeldaUtil.leerFechaTexto(fila, 3, "dd/MM/yyyy"),
                    CeldaUtil.leerFechaTexto(fila, 4, "dd/MM/yyyy"),
                    normalizarMoneda(CeldaUtil.leerTexto(fila, 5)),
                    CeldaUtil.leerDecimal(fila, 6),
                    null   // Scotiabank no trae estado -> el service lo homologa a VIGENTE
            ));
        }
        return resultado;
    }

    private int buscarFilaEncabezado(Sheet hoja) {
        for (int i = 0; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            if (CeldaUtil.leerTexto(fila, 0).contains("Banco")) {
                return i;
            }
        }
        return -1;
    }

    private String normalizarMoneda(String moneda) {
        if (moneda == null) return "USD";
        String m = moneda.trim().toUpperCase();
        if (m.contains("S/") || m.contains("SOL") || m.equals("PEN")) return "PEN";
        return "USD"; // "$", "US$", "Dólares" -> USD
    }

    private String textoDeFila(Row fila) {
        StringBuilder sb = new StringBuilder();
        short ultima = fila.getLastCellNum();
        for (int c = 0; c < ultima; c++) {
            Cell celda = fila.getCell(c);
            if (celda != null && celda.getCellType() == CellType.STRING) {
                sb.append(celda.getStringCellValue()).append(" | ");
            }
        }
        return sb.toString();
    }
}
