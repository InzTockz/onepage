package com.battilana.onepage.util.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CeldaUtil {

    private static final List<DateTimeFormatter> FORMATOS_FECHA = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

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
//                        .replace("$")
                        .replace(",", "")
                        .trim();
                yield texto.isEmpty() ? BigDecimal.ZERO : new BigDecimal(texto);
            }
            default -> BigDecimal.ZERO;
        };
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

    // Lee fecha tanto si Excel la guardó como número (Date) como si viene en texto (HTML)
    public static LocalDate leerFechaExcel(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        if (celda == null) return null;
        return switch (celda.getCellType()) {
            case NUMERIC -> {
                LocalDateTime ldt = celda.getLocalDateTimeCellValue();
                yield ldt != null ? ldt.toLocalDate() : null;
            }
            case STRING -> parsearFechaFlexible(celda.getStringCellValue().trim());
            default -> null;
        };
    }

    // Intenta parsear un texto de fecha probando varios formatos conocidos
    private static LocalDate parsearFechaFlexible(String texto) {
        if (texto == null || texto.isEmpty()) return null;
        for (DateTimeFormatter fmt : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(texto, fmt);
            } catch (DateTimeParseException ignorada) {
                // no era este formato, probamos el siguiente
            }
        }
        return null; // ningún formato coincidió
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

    public static String primeraLinea(Row fila, int columna){
        String texto = leerTexto(fila, columna);
        if(texto.isEmpty()) return "";
        return texto.split("\\r?\\n")[0].trim();
    }
}
