package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BcpParser implements BancoParser{

    // Fila 4: headers
    // 0: Nº Letra/Factura | 1: Nº Único | 2: Aceptante-Nombre | 3: Aceptante-Documento
    // 4: Vencimiento | 5: Monto | 6: Estado | 7: Causal
    // 8: Fecha de Ingreso | 9: Fecha de Descargo
    // 10: Interés | 11: Comisiones | 12: Portes | 13: Protesto

    @Override
    public boolean coincideFormato(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        // Huella BCP: cabecera "Letra / Factura" junto con "Aceptante" (misma fila)
        return CeldaUtil.existeFilaConTokens(hoja, 15, "Letra / Factura", "Aceptante");
    }

    @Override
    public List<PagoNormalizadoDto> parsear(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        List<PagoNormalizadoDto> resultado = new ArrayList<>();

        // Buscar la fila de encabezados para saber dónde empieza la data
        int filaInicio = buscarFilaEncabezado(hoja);
        if (filaInicio == -1) return resultado;

        for (int i = filaInicio + 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;

            String nroFactura = CeldaUtil.leerTexto(fila, 0);
            if (nroFactura.isEmpty()) continue;

            // BCP combina Estado + Causal: "Descargada" + "Cancelado"
            String estado = CeldaUtil.leerTexto(fila, 6);
            String causal = CeldaUtil.leerTexto(fila, 7);
            String estadoCompleto = causal.isEmpty() ? estado : causal;

            PagoNormalizadoDto dto = new PagoNormalizadoDto(
                    CeldaUtil.leerTexto(fila, 1),                     // nroTransaccion (Nº Único)
                    nroFactura,                                        // nroFactura
                    CeldaUtil.leerTexto(fila, 2),                     // aceptante
                    CeldaUtil.leerFechaTexto(fila, 9, "dd/MM/yyyy"),  // fechaOperacion (Fecha Descargo)
                    CeldaUtil.leerFechaTexto(fila, 4, "dd/MM/yyyy"),  // fechaVencimiento
                    CeldaUtil.leerFechaTexto(fila, 8, "dd/MM/yyyy"),  // fechaIngresoBanco
                    "Dolares",                                         // moneda
                    null,                                              // importeIngreso (BCP no lo tiene)
                    CeldaUtil.leerDecimal(fila, 5),                   // importe (Monto, con "US$")
                    CeldaUtil.leerDecimal(fila, 10),                  // interes
                    CeldaUtil.leerDecimal(fila, 11),                  // comision
                    CeldaUtil.leerDecimal(fila, 12),                  // gastos (Portes)
                    estadoCompleto                                     // estadoOriginal
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
                    && celda.getStringCellValue().contains("Letra / Factura")) {
                return i;
            }
        }
        return -1;
    }
}
