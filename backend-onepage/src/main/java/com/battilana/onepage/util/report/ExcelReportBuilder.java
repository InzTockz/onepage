package com.battilana.onepage.util.report;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.battilana.onepage.dto.facturas.FacturasPorCobrarResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExcelReportBuilder {

    //Formato que viene de la API
    private static final DateTimeFormatter FMT_API = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //Formato que se muestra en el excel
    private static final DateTimeFormatter FMT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] HEADERS = {
            "RUC", "RAZÓN SOCIAL", "NRO SAP", "DOCUMENTO", "EMISIÓN", "VENCIMIENTO",
            "MONEDA", "IMPORTE", "SALDO", "CONSULTOR", "LÍNEA CRÉDITO",
            "FECHA HOY", "DÍAS VENCIMIENTO", "ESTADO VENCE"
    };

    private static final List<String> BUCKETS = List.of(
            "No Vencido",
            "0 - 30",
            "31 - 45",
            "46 - 60",
            "61 - 90",
            "91 - 180",
            "+ 180"
    );

    public byte[] build(List<FacturasPorCobrarClientResponse> facturas) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Styles styles = new Styles(workbook);

            Map<String, List<FacturasPorCobrarClientResponse>> agrupado = facturas.stream()
                    .collect(Collectors.groupingBy(
                            f -> estadoVence(calcularDias(f.vencimiento()))
                    ));

            for (String bucket : BUCKETS) {
                List<FacturasPorCobrarClientResponse> grupo =
                        agrupado.getOrDefault(bucket, List.of());
                if (!grupo.isEmpty()) {
                    escribirHoja(workbook, styles, bucket, grupo);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();
        }
    }

    private void escribirHoja(SXSSFWorkbook workbook, Styles styles,
                              String nombreHoja,
                              List<FacturasPorCobrarClientResponse> facturas) {
        Sheet sheet = workbook.createSheet(nombreHoja);
        int rowNum = 0;

        // Fila 1: Consultor
        Row consultorRow = sheet.createRow(rowNum++);
        Cell consultorCell = consultorRow.createCell(0);
        consultorCell.setCellValue("Consultor: " + facturas.get(0).vendedor());
        consultorCell.setCellStyle(styles.title);
        rowNum++; // fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowNum++);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(styles.header);
        }

        // Datos
        String fechaHoy = LocalDate.now().format(FMT_DISPLAY);

        for (FacturasPorCobrarClientResponse f : facturas) {
            Row row = sheet.createRow(rowNum++);
            long dias = calcularDias(f.vencimiento());

            row.createCell(0).setCellValue(f.ruc());
            row.createCell(1).setCellValue(f.nombre());
            row.createCell(2).setCellValue(f.documento());
            row.createCell(3).setCellValue(f.comprobante());
//            row.createCell(4).setCellValue(f.emision());
//            row.createCell(5).setCellValue(f.vencimiento());
            row.createCell(4).setCellValue(formatearFecha(f.emision()));
            row.createCell(5).setCellValue(formatearFecha(f.vencimiento()));
            row.createCell(6).setCellValue(f.moneda());
            setCurrencyCell(row, 7,  f.importe(), styles.number);
            setCurrencyCell(row, 8,  f.saldo(),   styles.number);
            row.createCell(9).setCellValue(f.vendedor());
            setCurrencyCell(row, 10,  f.lc(),      styles.number);
            row.createCell(11).setCellValue(fechaHoy);
            row.createCell(12).setCellValue(dias);
            row.createCell(13).setCellValue(estadoVence(dias));
        }
    }

    private long calcularDias(String vencimiento) {
        return ChronoUnit.DAYS.between(
                LocalDate.parse(vencimiento, FMT_API),
                LocalDate.now()
        );
    }

    private String estadoVence(long dias) {
        if (dias <= 0)   return "No Vencido";
        if (dias <= 30)  return "0 - 30";
        if (dias <= 45)  return "31 - 45";
        if (dias <= 60)  return "46 - 60";
        if (dias <= 90)  return "61 - 90";
        if (dias <= 180) return "91 - 180";
        return "+ 180";
    }

    private void setCurrencyCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value.doubleValue() : 0.0);
        cell.setCellStyle(style);
    }

    private static class Styles {
        final CellStyle title;
        final CellStyle header;
        final CellStyle number;

        Styles(SXSSFWorkbook wb){
            //titulo consultor
            title = wb.createCellStyle();
            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 12);
            title.setFont(titleFont);

            // Header columnas
            header = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            header.setFont(headerFont);
            ((XSSFCellStyle) header).setFillForegroundColor(
                    new XSSFColor(new byte[]{(byte) 26, (byte) 46, (byte) 113}, null)
            );
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setAlignment(HorizontalAlignment.CENTER);
            header.setBorderBottom(BorderStyle.THIN);

            // Números
            number = wb.createCellStyle();
            DataFormat fmt = wb.createDataFormat();
            number.setDataFormat(fmt.getFormat("#,##0.00"));
            number.setAlignment(HorizontalAlignment.RIGHT);
        }
    }

    private String formatearFecha(String fecha) {
        return LocalDate.parse(fecha, FMT_API).format(FMT_DISPLAY);
    }
}
