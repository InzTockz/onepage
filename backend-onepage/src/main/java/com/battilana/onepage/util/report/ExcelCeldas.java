package com.battilana.onepage.util.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ExcelCeldas {

    private ExcelCeldas(){}

    /** Escribe un BigDecimal como número con estilo (0.0 si es null). */
    public static void moneda(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value.doubleValue() : 0.0);
        cell.setCellStyle(style);
    }

    /** Reformatea una fecha de un patrón a otro (ej: yyyy-MM-dd -> dd/MM/yyyy). */
    public static String formatearFecha(String fecha, DateTimeFormatter origen, DateTimeFormatter destino) {
        return LocalDate.parse(fecha, origen).format(destino);
    }
}
