package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoVigenteNormalizadoDto;
import com.battilana.onepage.entity.FacturaClienteEntity;
import com.battilana.onepage.repository.FacturaClienteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterbankVigenteParser implements BancoParser<PagoVigenteNormalizadoDto>{

    // Encabezados en fila 13; datos desde fila 15. Columnas físicas (0-based),
    // corridas por las celdas combinadas. Sacadas del archivo real:
    private static final int COL_DOCUMENTO  = 1;   // "Factura\n0075227567"
    private static final int COL_FECHA_ING  = 5;   // dd/MM/yyyy (texto)
    private static final int COL_FECHA_VENC = 11;  // dd/MM/yyyy (texto)
    private static final int COL_CLIENTE    = 16;  // "NOMBRE\nRUC ..."
    private static final int COL_MONTO_ORIG = 20;  // numérico
    private static final int COL_ESTADO     = 28;  // "Vigente"

    private final FacturaClienteRepository facturaClienteRepository;

    /**
     * 1: Documento y código interno | 2: Fecha de ingreso | 3: Cliente |
     * 4: Monto original | 5: saldo | 6: Estado
     */
    @Override
    public boolean coincideFormato(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        return CeldaUtil.existeFilaConTokens(hoja, 25, "Documento y")
                && CeldaUtil.existeFilaConTokens(hoja, 25, "Monto original");
    }

    @Override
    public List<PagoVigenteNormalizadoDto> parsear(Workbook workbook) {

        Sheet hoja = workbook.getSheetAt(0);
        List<PagoVigenteNormalizadoDto> resultado = new ArrayList<>();

        int filaEncabezado = buscarFilaEncabezado(hoja);
        if(filaEncabezado == -1) return resultado;

        for(int i = filaEncabezado -1 ; i <=hoja.getLastRowNum(); i++){
            Row fila = hoja.getRow(i);
            if(fila == null ) continue;

            String nroUniuco = CeldaUtil.leerTexto(fila, COL_DOCUMENTO)
                    .replaceAll("\\D", "")
                    .replaceFirst("^0+", "");
            if(nroUniuco.isEmpty()) continue;

            String aceptante = CeldaUtil.primeraLinea(fila, COL_CLIENTE);
            if(aceptante.isEmpty()) continue;

            //BUSCAR POR EL PERIODO MAYOR LA FACTURA VINCULADA A ESTE
            FacturaClienteEntity facturaCoincidente = this.facturaClienteRepository
                    .buscarFacturasPorAnioYPeriodo(LocalDate.now().getYear(), LocalDate.now().getMonthValue()-1)
                    .stream()
                    .filter(ft -> ft.getImporte() != null
                            && ft.getImporte().compareTo(CeldaUtil.leerDecimal(fila, COL_MONTO_ORIG)) == 0)
                    .findFirst()
                    .orElse(null);

            PagoVigenteNormalizadoDto dto = new PagoVigenteNormalizadoDto(
                    nroUniuco,
                    facturaCoincidente != null ? facturaCoincidente.getComprobante():"N.E",
                    aceptante,
                    CeldaUtil.leerFechaTexto(fila, COL_FECHA_ING, "dd/MM/yyyy"),
                    CeldaUtil.leerFechaTexto(fila, COL_FECHA_VENC, "dd/MM/yyyy"),
                    "Dolares",
                    CeldaUtil.leerDecimal(fila, COL_MONTO_ORIG),
                    CeldaUtil.leerTexto(fila, COL_ESTADO),
                    null
            );
            resultado.add(dto);
        }

        return resultado;
    }

    private int buscarFilaEncabezado(Sheet hoja) {
        int limite = Math.min(20, hoja.getLastRowNum());
        for (int i = 0; i <= limite; i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            if (textoDeFila(fila).contains("Documento y")) return i;
        }
        return -1;
    }

    private String textoDeFila(Row fila) {
        StringBuilder sb = new StringBuilder();
        short ultima = fila.getLastCellNum();
        for (int c = 0; c < ultima; c++) {
            sb.append(CeldaUtil.leerTexto(fila, c)).append(" ");
        }
        return sb.toString();
    }
}
