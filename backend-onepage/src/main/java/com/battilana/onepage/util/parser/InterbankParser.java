package com.battilana.onepage.util.parser;

import com.battilana.onepage.dto.pago.PagoNormalizadoDto;
import com.battilana.onepage.entity.FacturaClienteEntity;
import com.battilana.onepage.repository.FacturaClienteRepository;
import com.battilana.onepage.service.PagoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class InterbankParser implements BancoParser<PagoNormalizadoDto> {

    /**
     * LAS FILAS TIENEN FILAS COMBINADAS DE SOLO 2.
     * 1: Fecha de Proceso | 2: T/O | 3: N° IB | 4: N° Cedente | 5: Girado | 6: Vcto. | 7: Dias |
     * 8: Plz Cust | 9: C/P | 10: Importe del documento | 11: % Int Dcto | 12: Intereses |
     * 13: Amort. | 14: Nuevo importe | 15: Nuevo vcto. | 16: Interes | 17: Total de abonos |
     * 18: Comision | 19: Gastos port. / proc. | 20: Total de cargos
     */

    // Indices FISICOS de las columnas de datos.
    // Por las celdas combinadas no son antiguos (0.1.2...): cada dato vive en
    // la celda superior-izquierda de su bloque combinado. Sacados del archivo real
    private static final int COL_FECHA_PROCESO = 2;
    private static final int COL_TIPO_OPERACION = 6;
    private static final int COL_NRO_IB = 10;
    private static final int COL_GIRADO = 22;
    private static final int COL_IMPORTE = 40;

    private final FacturaClienteRepository facturaClienteRepository;

    public InterbankParser(FacturaClienteRepository facturaClienteRepository) {
        this.facturaClienteRepository = facturaClienteRepository;
    }

    @Override
    public boolean coincideFormato(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);

        //Huella Interbank: encabezados propios "Girado" + "Cedente" (evitamos el "°" a propósito)
        return CeldaUtil.existeFilaConTokens(hoja, 20, "Girado", "Cedente");
    }

    @Override
    public List<PagoNormalizadoDto> parsear(Workbook workbook) {
        Sheet hoja = workbook.getSheetAt(0);
        List<PagoNormalizadoDto> resultado = new ArrayList<>();

        for (int i = 0; i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) continue;
            // fila de datos valida: el nro IB debe ser numerico
            // Esto descarta solito el preambulo, la fila vacia y el resumen "Ultima liquidacion"
            String nroIb = CeldaUtil.leerTexto(fila, COL_NRO_IB);
            if (nroIb.isEmpty() || !nroIb.matches("\\d+")) continue;

            // Debe tener aceptante (Girado)
            String aceptante = CeldaUtil.leerTexto(fila, COL_GIRADO);
            if (aceptante.isEmpty()) continue;

            // T/O 2 => "Cancelacion (tu HomologacionEstado ya lo mapea a CANCELADO)
            String tipoOperacion = CeldaUtil.leerTexto(fila, COL_TIPO_OPERACION);
            String estadoOriginal = "2".equals(tipoOperacion) ? "Cancelacion" : tipoOperacion;

            BigDecimal importeFila = CeldaUtil.leerDecimal(fila, COL_IMPORTE);

            //BUSCAR POR EL PERIODO MAYOR LA FACTURA VINCULADA A ESTE
            FacturaClienteEntity facturaCoincidente = this.facturaClienteRepository
                    .buscarFacturasPorAnioYPeriodo(LocalDate.now().getYear(), LocalDate.now().getMonthValue()-1)
                    .stream()
                    .filter(ft -> ft.getImporte() != null
                            && ft.getImporte().compareTo(importeFila) == 0)
                    .findFirst()
                    .orElse(null);

            PagoNormalizadoDto dto = new PagoNormalizadoDto(
                    nroIb,
                    facturaCoincidente != null ? facturaCoincidente.getComprobante():"N.E",
                    aceptante,
                    CeldaUtil.leerFechaTexto(fila, COL_FECHA_PROCESO, "dd/MM/yyyy"),
                    null,
                    null,
                    "Dolares",
                    null,
                    CeldaUtil.leerDecimal(fila, COL_IMPORTE),
                    null,
                    null,
                    null,
                    estadoOriginal,
                    null
            );
            resultado.add(dto);
        }

        return resultado;
    }
}
