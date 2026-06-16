package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class BbvaParser implements BancoParser {

    // Columnas del detalle (fila 15 en adelante)
    // 0: F. Venc. | 1: Nro. Banco | 2: Nro. Original | 3: Aceptante
    // 4: Docs. Ingres. | 5: Docs. Descarg | 6: Interés | 7: Comisión | 8: Gastos | 9: Sit.

    @Override
    public List<PagoNormalizadoDto> parsear(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        List<PagoNormalizadoDto> resultado = new ArrayList<>();

        // 1. Extraer fecha de operación buscando dinámicamente
        LocalDate fechaOperacion = extraerFechaOperacion(hoja);

        // 2. Buscar la fila de encabezados (donde aparece "F. Venc.")
        int filaEncabezado = buscarFilaEncabezado(hoja);
        if (filaEncabezado == -1) {
            log.warn("No se encontró la fila de encabezados en el archivo BBVA");
            return resultado;
        }

        // 3. Recorrer SOLO desde encabezado+1 en adelante
        for (int i = filaEncabezado + 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;

            // 4. Si encontramos "Situación", es la leyenda → dejamos de leer
            String primeraCelda = CeldaUtil.leerTexto(fila, 0);
            if (primeraCelda.contains("Situación") || primeraCelda.contains("Situacion")) {
                break;
            }

            // 5. Validar que sea fila de datos: Nro. Banco (col 1) debe ser numérico
            String nroBanco = CeldaUtil.leerTexto(fila, 1);
            if (nroBanco.isEmpty() || !nroBanco.matches("\\d+")) continue;

            // 6. Validar que tenga aceptante
            String aceptante = CeldaUtil.leerTexto(fila, 3);
            if (aceptante.isEmpty()) continue;

            PagoNormalizadoDto dto = new PagoNormalizadoDto(
                    nroBanco,
                    CeldaUtil.leerTexto(fila, 2),
                    aceptante,
                    fechaOperacion,
                    CeldaUtil.leerFechaExcel(fila, 0),
                    null,
                    "Dolares",
                    CeldaUtil.leerDecimal(fila, 4),
                    CeldaUtil.leerDecimal(fila, 5),
                    CeldaUtil.leerDecimal(fila, 6),
                    CeldaUtil.leerDecimal(fila, 7),
                    CeldaUtil.leerDecimal(fila, 8),
                    CeldaUtil.leerTexto(fila, 9)
            );
            resultado.add(dto);
        }

        return resultado;
    }

    private LocalDate extraerFechaOperacion(Sheet hoja) {
        // Buscar la fila que contiene "Fecha Operación"
        for (int i = 0; i <= 20; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            Cell celda = fila.getCell(0);
            if (celda == null || celda.getCellType() != CellType.STRING) continue;

            String texto = celda.getStringCellValue();
            if (texto.contains("Fecha Operación")) {
                // "Fecha Operación : 2026-06-09   Docs : 2"
                int inicio = texto.indexOf(":") + 1;
                String fechaTexto = texto.substring(inicio).trim().split("\\s+")[0];
                return LocalDate.parse(fechaTexto); // yyyy-MM-dd por defecto
            }
        }
        return null;
    }

    private int buscarFilaEncabezado(Sheet hoja) {
        for (int i = 0; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            Cell celda = fila.getCell(0);
            if (celda == null || celda.getCellType() != CellType.STRING) continue;

            String texto = celda.getStringCellValue().trim();
            if (texto.contains("F. Venc")) {
                return i;
            }
        }
        return -1;
    }
}
