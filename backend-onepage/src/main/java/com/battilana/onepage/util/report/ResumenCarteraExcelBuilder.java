package com.battilana.onepage.util.report;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ResumenCarteraExcelBuilder {

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
            ExcelStyles styles = new ExcelStyles(workbook);

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

    private void escribirHoja(SXSSFWorkbook workbook, ExcelStyles styles,
                              String nombreHoja, List<FacturasPorCobrarClientResponse> facturas) {
        Sheet sheet = workbook.createSheet(nombreHoja);
        int rowNum = 0;

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
            row.createCell(4).setCellValue(ExcelCeldas.formatearFecha(f.emision(), FMT_API, FMT_DISPLAY));
            row.createCell(5).setCellValue(ExcelCeldas.formatearFecha(f.vencimiento(), FMT_API, FMT_DISPLAY));
            row.createCell(6).setCellValue(f.moneda());
            ExcelCeldas.moneda(row, 7,  f.importe(), styles.number);
            ExcelCeldas.moneda(row, 8,  f.saldo(),   styles.number);
            row.createCell(9).setCellValue(f.vendedor());
            ExcelCeldas.moneda(row, 10, f.lc(),      styles.number);
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
}
