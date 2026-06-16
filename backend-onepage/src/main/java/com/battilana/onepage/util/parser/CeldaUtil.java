package com.battilana.onepage.util.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CeldaUtil {

    // Lee texto de una celda sin importar si Excel la guardó como número o string
    public static String leerTexto(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        if (celda == null) return "";
        return switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) celda.getNumericCellValue());
            default -> "";
        };
    }

    // Lee un número como BigDecimal
    public static BigDecimal leerDecimal(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        if (celda == null) return BigDecimal.ZERO;
        return switch (celda.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(celda.getNumericCellValue());
            case STRING -> {
                String texto = celda.getStringCellValue()
                        .replace("US$", "")
                        .replace(",", "")
                        .trim();
                yield texto.isEmpty() ? BigDecimal.ZERO : new BigDecimal(texto);
            }
            default -> BigDecimal.ZERO;
        };
    }

    // Lee fecha que Excel guardó como tipo Date
    public static LocalDate leerFechaExcel(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        if (celda == null) return null;
        if (celda.getCellType() == CellType.NUMERIC) {
            // Excel guarda fechas como números internamente
            LocalDateTime ldt = celda.getLocalDateTimeCellValue();
            return ldt != null ? ldt.toLocalDate() : null;
        }
        return null;
    }

    // Lee fecha que viene como texto "dd/MM/yyyy"
    public static LocalDate leerFechaTexto(Row fila, int columna, String formato) {
        Cell celda = fila.getCell(columna);
        if (celda == null) return null;
        String texto = "";
        if (celda.getCellType() == CellType.STRING) {
            texto = celda.getStringCellValue().trim();
        } else if (celda.getCellType() == CellType.NUMERIC) {
            // Si Excel la interpretó como fecha numérica
            LocalDateTime ldt = celda.getLocalDateTimeCellValue();
            return ldt != null ? ldt.toLocalDate() : null;
        }
        if (texto.isEmpty()) return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(formato);
        return LocalDate.parse(texto, fmt);
    }
}
