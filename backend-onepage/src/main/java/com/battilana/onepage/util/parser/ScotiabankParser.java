package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScotiabankParser implements BancoParser{

    // Fila 6: headers
    // 0: N°. Banco | 1: Pagador/Adquiriente | 2: Fecha operación | 3: Fecha vencimiento
    // 4: Importe | 5: Situación | 6: Forma de Pago | 7: Descripción | 8: Documento proveedor

    @Override
    public List<PagoNormalizadoDto> parsear(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        List<PagoNormalizadoDto> resultado = new ArrayList<>();

        int filaInicio = buscarFilaEncabezado(hoja);
        if (filaInicio == -1) return resultado;

        for (int i = filaInicio + 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;

            String nroBanco = CeldaUtil.leerTexto(fila, 0);
            if (nroBanco.isEmpty() || !nroBanco.matches("\\d+")) continue;

            PagoNormalizadoDto dto = new PagoNormalizadoDto(
                    nroBanco,                                          // nroTransaccion
                    CeldaUtil.leerTexto(fila, 8),                     // nroFactura (Documento proveedor)
                    CeldaUtil.leerTexto(fila, 1).trim(),              // aceptante
                    CeldaUtil.leerFechaTexto(fila, 2, "dd/MM/yyyy"),  // fechaOperacion
                    CeldaUtil.leerFechaTexto(fila, 3, "dd/MM/yyyy"),  // fechaVencimiento
                    null,                                              // fechaIngresoBanco
                    "Dolares",                                         // moneda
                    null,                                              // importeIngreso
                    CeldaUtil.leerDecimal(fila, 4),                   // importe
                    null,                                              // interes (Scotiabank no lo tiene)
                    null,                                              // comision
                    null,                                              // gastos
                    CeldaUtil.leerTexto(fila, 5)                      // estadoOriginal (Situación)
            );
            resultado.add(dto);
        }

        return resultado;
    }

    private int buscarFilaEncabezado(Sheet hoja) {
        for (int i = 0; i <= 10; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            Cell celda = fila.getCell(0);
            if (celda != null && celda.getCellType() == CellType.STRING
                    && celda.getStringCellValue().contains("Banco")) {
                return i;
            }
        }
        return -1;
    }
}
