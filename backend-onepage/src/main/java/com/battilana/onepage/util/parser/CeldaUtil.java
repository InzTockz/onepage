package com.battilana.onepage.util.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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

    /**
     * Devuelve true si, dentro de las primeras {@code maxFilas} filas, existe UNA fila
     * cuyo texto contenga TODOS los tokens indicados. Sirve de "huella" del formato.
     */
    public static boolean existeFilaConTokens(Sheet hoja, int maxFilas, String... tokens) {
        int limite = Math.min(maxFilas, hoja.getLastRowNum());
        for (int i = 0; i <= limite; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            String textoFila = textoDeFila(fila);
            boolean contieneTodos = true;
            for (String token : tokens) {
                if (!textoFila.contains(token)) {
                    contieneTodos = false;
                    break;
                }
            }
            if (contieneTodos) return true;
        }
        return false;
    }

    private static String textoDeFila(Row fila) {
        StringBuilder sb = new StringBuilder();
        int ultima = fila.getLastCellNum();            // -1 si la fila está vacía
        for (int c = 0; c < ultima; c++) {
            Cell celda = fila.getCell(c);
            if (celda != null && celda.getCellType() == CellType.STRING) {
                sb.append(celda.getStringCellValue().trim()).append(" | ");
            }
        }
        return sb.toString();
    }
}
