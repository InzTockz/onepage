package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BcpVigenteParser implements BancoParser<PagoVigenteNormalizadoDto> {

    // Fila 5 (índice 4): encabezados. Datos desde la fila 6.
    // 0: Nº Letra/Factura | 1: Nº Único | 2: Aceptante-Nombre | 3: Aceptante-Documento
    // 4: Vencimiento | 5: Monto | 6: Estado | 7: Causal | 8: Fecha Ingreso | 9: Fecha Descargo ...
    private static final int COL_FACTURA        = 0;
    private static final int COL_UNICO          = 1;
    private static final int COL_ACEPTANTE      = 2;
    private static final int COL_VENCIMIENTO    = 4;
    private static final int COL_MONTO          = 5;
    private static final int COL_ESTADO         = 6;
    private static final int COL_FECHA_INGRESO  = 8;

    @Override
    public boolean coincideFormato(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        // Huella BCP: cabecera con "Letra / Factura" + "Aceptante"
        return CeldaUtil.existeFilaConTokens(hoja, 15, "Letra / Factura", "Aceptante");
    }

    @Override
    public List<PagoVigenteNormalizadoDto> parsear(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        List<PagoVigenteNormalizadoDto> resultado = new ArrayList<>();

        int filaEncabezado = buscarFilaEncabezado(hoja);
        if (filaEncabezado == -1) return resultado;

        String producto = leerProducto(hoja);

        for (int i = filaEncabezado + 1; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;

            // Fila válida: Nº Único numérico
            String nroUnico = CeldaUtil.leerTexto(fila, COL_UNICO);
            if (nroUnico.isEmpty() || !nroUnico.matches("\\d+")) continue;

            String aceptante = CeldaUtil.leerTexto(fila, COL_ACEPTANTE);
            if (aceptante.isEmpty()) continue;

            resultado.add(new PagoVigenteNormalizadoDto(
                    nroUnico,                                                        // nroUnico (Nº Único)
                    CeldaUtil.leerTexto(fila, COL_FACTURA),                          // nroFactura (Nº Letra/Factura)
                    aceptante,                                                       // aceptante
                    CeldaUtil.leerFechaTexto(fila, COL_FECHA_INGRESO, "dd/MM/yyyy"), // fechaIngreso
                    CeldaUtil.leerFechaTexto(fila, COL_VENCIMIENTO, "dd/MM/yyyy"),   // fechaVencimiento
                    "Dolares",                                                           // moneda (Monto en US$)
                    CeldaUtil.leerDecimal(fila, COL_MONTO),                          // importe (quita "US$" y comas)
                    CeldaUtil.leerTexto(fila, COL_ESTADO),                       // estadoOriginal ("Vigente")
                    producto
            ));
        }
        return resultado;
    }

    private int buscarFilaEncabezado(Sheet hoja) {
        int limite = Math.min(15, hoja.getLastRowNum());
        for (int i = 0; i <= limite; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            if (CeldaUtil.leerTexto(fila, 0).contains("Letra / Factura")) {
                return i;
            }
        }
        return -1;
    }

    private String leerProducto(Sheet hoja) {
        int limite = Math.min(10, hoja.getLastRowNum());
        for (int i = 0; i <= limite; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            if (CeldaUtil.leerTexto(fila, 0).equalsIgnoreCase("Producto")) {
                return CeldaUtil.leerTexto(fila, 1);
            }
        }
        return null;
    }
}
