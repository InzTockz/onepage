package com.battilana.onepage.controller;

import com.battilana.onepage.service.ReportesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ReportesController {

    private final ReportesService reportesService;

    @GetMapping("/facturas-totales")
    public ResponseEntity<byte[]> reporteGeneralDeFacturas() throws IOException {
        byte[] bytes = reportesService.reporteGeneralDeFacturas();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"reporte_facturas_totales.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(bytes);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarExcel(@RequestParam Integer vendedor) throws IOException {
        byte[] bytes = reportesService.generarExcel(vendedor);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"reporte_" + vendedor + ".xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(bytes);
    }
}
