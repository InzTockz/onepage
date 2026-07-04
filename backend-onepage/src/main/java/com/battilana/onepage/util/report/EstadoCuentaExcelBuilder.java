package com.battilana.onepage.util.report;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstadoCuentaExcelBuilder {

    // Colores del dashboard
    private static final byte[] NAVY = {(byte) 0x2E, (byte) 0x32, (byte) 0x47};
    private static final byte[] PINK = {(byte) 0xE6, (byte) 0x3E, (byte) 0x7A};
    private static final byte[] ORANGE = {(byte) 0xE8, (byte) 0x79, (byte) 0x2B};
    private static final byte[] WHITE = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    // --- Colores extra para la tabla ---
    private static final byte[] GRAY = {(byte) 0xD9, (byte) 0xD9, (byte) 0xD9};
    private static final byte[] DARK = {(byte) 0x22, (byte) 0x22, (byte) 0x22};

    private static final DateTimeFormatter FMT_API = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] TABLA_HEADERS = {
            "RAZÓN SOCIAL", "DÍAS FINAL", "DOCUMENTO", "EMISIÓN", "VENCIMIENTO", "N° ÚNICO", "BANCO",
            "No vencido", "Entre 0 y 30", "Entre 31 y 45", "Entre 46 y 60",
            "Entre 61 y 90", "Entre 91 y 180", "Mayor a 180", "Total general"
    };

    // Orden lógico de los 7 rangos (debe calzar con estadoVence)
    private static final List<String> BUCKETS = List.of(
            "No Vencido", "0 - 30", "31 - 45", "46 - 60", "61 - 90", "91 - 180", "+ 180"
    );

    private static final int COL_INI = 1;   // columna B
    private static final int COL_BUCKET0 = 8;   // columna I (primer rango)
    private static final int COL_TOTAL = 15;  // columna P
    private static final int FILA_TABLA = 17;  // fila donde empieza la tabla (Excel 18)

    private static final byte[]  LGRAY   = {(byte) 0xF2, (byte) 0xF2, (byte) 0xF2};

    private static final String[] FILTROS = {
            "No vencido", "Entre 0 y 30", "Entre 31 y 45", "Entre 46 y 60",
            "Entre 61 y 90", "Entre 91 y 180", "Mayor a 180"
    };

    public byte[] build(List<FacturasPorCobrarClientResponse> datos) throws IOException {
        // El nombre del consultor sale de la propia respuesta (viene filtrada por un vendedor)
        String consultor = datos.isEmpty() ? "" : datos.get(0).vendedor();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Estado de cuenta");
            sheet.setDisplayGridlines(false);
            anchos(sheet);

            escribirCabecera(wb, sheet, consultor);   // String: el nombre
            escribirBotonesFiltro(wb, sheet);
            insertarGraficos(wb, sheet, datos);
            escribirTabla(wb, sheet, datos);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    // ---------------------------------------------------------------------
// PASO 2 — Banda de cabecera (sin logo; recuadros de consultor y mes)
// ---------------------------------------------------------------------
    private void escribirCabecera(XSSFWorkbook wb, Sheet sheet, String consultor) {
        XSSFCellStyle banda  = estilo(wb, NAVY, null,                        HorizontalAlignment.LEFT);
        XSSFCellStyle titulo = estilo(wb, NAVY, fuente(wb, 22, true, WHITE), HorizontalAlignment.LEFT);
        XSSFCellStyle boxIzq = estilo(wb, PINK, fuente(wb, 12, true, WHITE), HorizontalAlignment.LEFT);
        XSSFCellStyle boxCen = estilo(wb, PINK, fuente(wb, 12, true, WHITE), HorizontalAlignment.CENTER);

        pintar(sheet, 0, 0, 8, 9, banda);
        bloque(sheet, 1, 1, 2, 5, "Estado de cuenta", titulo);
        bloque(sheet, 4, 2, 5, 5, "Consultor: " + consultor, boxIzq);   // ahora sí el nombre
        bloque(sheet, 4, 7, 5, 9, mesActual(), boxCen);
    }

    /**
     * Mes actual en español, ej. "Junio 2026".
     */
    private String mesActual() {
        String m = LocalDate.now().format(
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.of("es", "ES")));
        return Character.toUpperCase(m.charAt(0)) + m.substring(1);
    }

    // ---------------------------------------------------------------------
    // Helpers de estilo y celdas
    // ---------------------------------------------------------------------
    private XSSFCellStyle estilo(XSSFWorkbook wb, byte[] fondo, XSSFFont font, HorizontalAlignment h) {
        XSSFCellStyle s = wb.createCellStyle();
        if (fondo != null) {
            s.setFillForegroundColor(new XSSFColor(fondo, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (font != null) s.setFont(font);
        s.setAlignment(h);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private XSSFFont fuente(XSSFWorkbook wb, int size, boolean bold, byte[] color) {
        XSSFFont f = wb.createFont();
        f.setFontHeightInPoints((short) size);
        f.setBold(bold);
        if (color != null) f.setColor(new XSSFColor(color, null));
        return f;
    }

    /**
     * Pinta todas las celdas de un rango con el mismo estilo.
     */
    private void pintar(Sheet sheet, int r1, int c1, int r2, int c2, CellStyle style) {
        for (int r = r1; r <= r2; r++) {
            Row row = sheet.getRow(r) != null ? sheet.getRow(r) : sheet.createRow(r);
            for (int c = c1; c <= c2; c++) {
                Cell cell = row.getCell(c) != null ? row.getCell(c) : row.createCell(c);
                cell.setCellStyle(style);
            }
        }
    }

    /**
     * Pinta + combina un rango y le pone un texto en la celda superior izquierda.
     */
    private void bloque(Sheet sheet, int r1, int c1, int r2, int c2, String texto, XSSFCellStyle style) {
        pintar(sheet, r1, c1, r2, c2, style);
        if (r1 != r2 || c1 != c2) {
            sheet.addMergedRegion(new CellRangeAddress(r1, r2, c1, c2));
        }
        sheet.getRow(r1).getCell(c1).setCellValue(texto);
    }

    private void anchos(Sheet sheet) {
        int[] w = {3, 24, 11, 13, 11, 12, 11, 13, 11, 11, 11, 11, 11, 12, 11, 13}; // A..P
        for (int c = 0; c < w.length; c++) sheet.setColumnWidth(c, w[c] * 256);
    }

    // ---------------------------------------------------------------------
// PASO 3 — Matriz de aging (agrupada por razón social)
// ---------------------------------------------------------------------
    private void escribirTabla(XSSFWorkbook wb, Sheet sheet, List<FacturasPorCobrarClientResponse> datos) {
        XSSFCellStyle hdr = estilo(wb, PINK, fuente(wb, 9, true, WHITE), HorizontalAlignment.CENTER);
        hdr.setWrapText(true);
        bordes(hdr);
        XSSFCellStyle celda = estilo(wb, null, fuente(wb, 9, false, DARK), HorizontalAlignment.LEFT);
        bordes(celda);
        XSSFCellStyle num = estilo(wb, null, fuente(wb, 9, false, DARK), HorizontalAlignment.RIGHT);
        num.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        bordes(num);
        XSSFCellStyle sub = estilo(wb, GRAY, fuente(wb, 9, true, DARK), HorizontalAlignment.LEFT);
        bordes(sub);
        XSSFCellStyle subNum = estilo(wb, GRAY, fuente(wb, 9, true, DARK), HorizontalAlignment.RIGHT);
        subNum.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        bordes(subNum);

        int fila = FILA_TABLA;

        // Encabezados
        Row hr = sheet.createRow(fila++);
        for (int i = 0; i < TABLA_HEADERS.length; i++) {
            Cell c = hr.createCell(COL_INI + i);
            c.setCellValue(TABLA_HEADERS[i]);
            c.setCellStyle(hdr);
        }

        // Agrupar por razón social conservando el orden
        Map<String, List<FacturasPorCobrarClientResponse>> porCliente = datos.stream()
                .collect(Collectors.groupingBy(FacturasPorCobrarClientResponse::nombre,
                        LinkedHashMap::new, Collectors.toList()));

        BigDecimal[] granTotal = nuevoAcumulador();

        for (Map.Entry<String, List<FacturasPorCobrarClientResponse>> e : porCliente.entrySet()) {
            String cliente = e.getKey();
            BigDecimal[] subtotal = nuevoAcumulador();

            for (FacturasPorCobrarClientResponse f : e.getValue()) {
                long dias = calcularDias(f.vencimiento());
                int idx = BUCKETS.indexOf(estadoVence(dias));       // 0..6
                BigDecimal saldo = f.saldo() != null ? f.saldo() : BigDecimal.ZERO;

                Row r = sheet.createRow(fila++);
                texto(r, COL_INI, cliente, celda);
                Cell cd = r.createCell(COL_INI + 1);
                cd.setCellValue(dias);
                cd.setCellStyle(celda);
                texto(r, COL_INI + 2, f.comprobante(), celda);
                texto(r, COL_INI + 3, ExcelCeldas.formatearFecha(f.emision(), FMT_API, FMT_DISPLAY), celda);
                texto(r, COL_INI + 4, ExcelCeldas.formatearFecha(f.vencimiento(), FMT_API, FMT_DISPLAY), celda);
                texto(r, COL_INI + 5, "", celda);   // N° Único (en blanco por ahora)
                texto(r, COL_INI + 6, "", celda);   // Banco (en blanco por ahora)

                for (int b = 0; b < 7; b++) {       // el saldo solo entra en su rango
                    Cell cc = r.createCell(COL_BUCKET0 + b);
                    if (b == idx) cc.setCellValue(saldo.doubleValue());
                    cc.setCellStyle(num);
                }
                Cell ct = r.createCell(COL_TOTAL);
                ct.setCellValue(saldo.doubleValue());
                ct.setCellStyle(num);

                subtotal[idx] = subtotal[idx].add(saldo);
                subtotal[7] = subtotal[7].add(saldo);
            }

            escribirFilaTotal(sheet, fila++, "Total " + cliente, subtotal, sub, subNum);
            for (int b = 0; b < 8; b++) granTotal[b] = granTotal[b].add(subtotal[b]);
        }

        escribirFilaTotal(sheet, fila, "TOTAL GENERAL", granTotal, sub, subNum);
    }

    /**
     * Escribe una fila de subtotal/total: rótulo + montos por rango + total.
     */
    private void escribirFilaTotal(Sheet sheet, int fila, String rotulo, BigDecimal[] acum,
                                   CellStyle base, CellStyle baseNum) {
        Row r = sheet.createRow(fila);
        for (int c = COL_INI; c <= COL_TOTAL; c++) r.createCell(c).setCellStyle(base);
        r.getCell(COL_INI).setCellValue(rotulo);
        for (int b = 0; b < 7; b++) {
            Cell cc = r.getCell(COL_BUCKET0 + b);
            if (acum[b].signum() != 0) cc.setCellValue(acum[b].doubleValue());
            cc.setCellStyle(baseNum);
        }
        Cell ct = r.getCell(COL_TOTAL);
        ct.setCellValue(acum[7].doubleValue());
        ct.setCellStyle(baseNum);
    }

    private BigDecimal[] nuevoAcumulador() {
        BigDecimal[] a = new BigDecimal[8];        // 7 rangos + total
        Arrays.fill(a, BigDecimal.ZERO);
        return a;
    }

    private void texto(Row r, int col, String val, CellStyle style) {
        Cell c = r.createCell(col);
        c.setCellValue(val != null ? val : "");
        c.setCellStyle(style);
    }

    private void bordes(XSSFCellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private long calcularDias(String vencimiento) {
        return ChronoUnit.DAYS.between(LocalDate.parse(vencimiento, FMT_API), LocalDate.now());
    }

    private String estadoVence(long dias) {
        if (dias <= 0) return "No Vencido";
        if (dias <= 30) return "0 - 30";
        if (dias <= 45) return "31 - 45";
        if (dias <= 60) return "46 - 60";
        if (dias <= 90) return "61 - 90";
        if (dias <= 180) return "91 - 180";
        return "+ 180";
    }

    // ---------------------------------------------------------------------
// PASO 4 — Gráficos (imágenes JFreeChart)
// ---------------------------------------------------------------------
    private void insertarGraficos(XSSFWorkbook wb, Sheet sheet,
                                  List<FacturasPorCobrarClientResponse> datos) throws IOException {
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();

        // TOP 10 -> arriba a la derecha (cols K..P, filas 2..14)
        int idxTop = wb.addPicture(top10Png(datos), Workbook.PICTURE_TYPE_PNG);
        drawing.createPicture(new XSSFClientAnchor(0, 0, 220000, 124000, 10, 1, 16, 14), idxTop);

        // Dona -> zona de filtros (cols G..J, filas 11..17)
        int idxDona = wb.addPicture(donaPng(datos), Workbook.PICTURE_TYPE_PNG);
        drawing.createPicture(new XSSFClientAnchor(0, 0, 0, 0, 6, 10, 10, 16), idxDona);
    }

    /** Barras horizontales: 10 clientes con más saldo vencido. */
    private byte[] top10Png(List<FacturasPorCobrarClientResponse> datos) throws IOException {
        Map<String, BigDecimal> porCliente = new LinkedHashMap<>();
        for (FacturasPorCobrarClientResponse f : datos) {
            if (calcularDias(f.vencimiento()) > 0) {                 // solo vencido
                BigDecimal s = f.saldo() != null ? f.saldo() : BigDecimal.ZERO;
                porCliente.merge(f.nombre(), s, BigDecimal::add);
            }
        }
        List<Map.Entry<String, BigDecimal>> top = porCliente.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .toList();

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
//        for (int i = top.size() - 1; i >= 0; i--)                    // el mayor arriba
//            ds.addValue(top.get(i).getValue(), "Vencido", top.get(i).getKey());
        for (Map.Entry<String, BigDecimal> e : top)                  // "top" ya viene de mayor a menor
            ds.addValue(e.getValue(), "Vencido", e.getKey());        // el mayor primero -> el mayor arriba

        JFreeChart chart = ChartFactory.createBarChart(
                "TOP 10 Vencidos", null, null, ds, PlotOrientation.HORIZONTAL, false, false, false);
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        CategoryAxis domain = plot.getDomainAxis();
        domain.setTickLabelFont(new Font("Arial", Font.PLAIN, 8));   // letra un poco más pequeña
        domain.setMaximumCategoryLabelWidthRatio(0.9f);              // deja que el nombre completo se muestre
        domain.setMaximumCategoryLabelLines(2);

        // Umbral: barras largas (el label cabe dentro) -> blanco; cortas (sale afuera) -> oscuro
        double max = top.isEmpty() ? 0 : top.get(0).getValue().doubleValue();
        final double umbral = max * 0.15;

        BarRenderer r = new BarRenderer() {
            @Override
            public Paint getItemLabelPaint(int row, int column) {
                Number v = ds.getValue(row, column);
                double val = v == null ? 0 : v.doubleValue();
                return val >= umbral ? Color.WHITE : new Color(0x22, 0x22, 0x22);
            }
        };
        plot.setRenderer(r);

        r.setSeriesPaint(0, new Color(226, 0, 0));                    // rojo
        r.setBarPainter(new StandardBarPainter());                    // color plano, sin degradado
        r.setShadowVisible(false);
        r.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#,##0.00")));
        r.setDefaultItemLabelsVisible(true);

        // --- etiqueta DENTRO de la barra (con fallback afuera para las cortas) ---
        r.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 9));
        r.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.INSIDE3, TextAnchor.CENTER_RIGHT));
        r.setPositiveItemLabelPositionFallback(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT));

        return aPng(chart.createBufferedImage(546, 315));
    }

    /** Dona con % vencido al centro + "VENCIDO USD" y el monto al lado. */
    private byte[] donaPng(List<FacturasPorCobrarClientResponse> datos) throws IOException {
        BigDecimal total   = sumaSaldo(datos, false);
        BigDecimal vencido = sumaSaldo(datos, true);
        double pct = total.signum() == 0 ? 0
                : vencido.divide(total, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        ds.setValue("Vencido", pct);
        ds.setValue("No vencido", 100 - pct);

        JFreeChart chart = ChartFactory.createRingChart(null, ds, false, false, false);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionPaint("Vencido", new Color(226, 0, 0));
        plot.setSectionPaint("No vencido", new Color(46, 50, 71));   // navy
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionDepth(0.35);
        plot.setLabelGenerator(null);                                // sin etiquetas alrededor
        chart.setBackgroundPaint(Color.WHITE);

        BufferedImage ring = chart.createBufferedImage(170, 170);

        // Lienzo: dona a la izquierda + texto a la derecha
        BufferedImage canvas = new BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE); g.fillRect(0, 0, 320, 180);
        g.drawImage(ring, 5, 5, null);

        // "22%" al centro de la dona
        g.setColor(new Color(46, 50, 71));
        g.setFont(new Font("Arial", Font.BOLD, 26));
        String p = Math.round(pct) + "%";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(p, 90 - fm.stringWidth(p) / 2, 90 + (fm.getAscent() - fm.getDescent()) / 2);

        // Texto a la derecha
        g.setColor(new Color(226, 0, 0));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("VENCIDO USD", 185, 82);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(new DecimalFormat("#,##0.00").format(vencido), 185, 108);
        g.dispose();

        return aPng(canvas);
    }

    private BigDecimal sumaSaldo(List<FacturasPorCobrarClientResponse> datos, boolean soloVencido) {
        BigDecimal t = BigDecimal.ZERO;
        for (FacturasPorCobrarClientResponse f : datos) {
            boolean venc = calcularDias(f.vencimiento()) > 0;
            if (!soloVencido || venc)
                t = t.add(f.saldo() != null ? f.saldo() : BigDecimal.ZERO);
        }
        return t;
    }

    private byte[] aPng(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        return out.toByteArray();
    }

    // ---------------------------------------------------------------------
// PASO 5 — Botones de filtro (decorativos)
// ---------------------------------------------------------------------
    private void escribirBotonesFiltro(XSSFWorkbook wb, Sheet sheet) {
        XSSFCellStyle zona   = estilo(wb, LGRAY, null,                        HorizontalAlignment.LEFT);
        XSSFCellStyle titulo = estilo(wb, LGRAY, fuente(wb, 11, true, DARK),  HorizontalAlignment.LEFT);
        XSSFCellStyle boton  = estilo(wb, PINK,  fuente(wb, 9, true, WHITE),  HorizontalAlignment.CENTER);

        // Banda gris del área de filtros (cols B..J, filas 10..17)
        pintar(sheet, 9, 1, 16, 9, zona);

        // Rótulo
        bloque(sheet, 9, 1, 9, 3, "\u25BE  Filtro", titulo);

        // 7 botones, 2 por fila (cols B-C y D-E)
        int fila = 11, col = 1;
        for (String f : FILTROS) {
            bloque(sheet, fila, col, fila, col + 1, f, boton);   // botón de 2 columnas
            if (col == 1) {
                col = 3;                 // segunda columna de la fila
            } else {
                col = 1; fila++;         // salta a la siguiente fila
            }
        }
    }
}
