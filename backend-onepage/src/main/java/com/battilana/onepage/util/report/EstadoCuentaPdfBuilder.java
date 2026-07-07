package com.battilana.onepage.util.report;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EstadoCuentaPdfBuilder {

    private final EstadoCuentaGraficos graficos;

    private static final Color NAVY  = new Color(0x2E, 0x32, 0x47);
    private static final Color PINK  = new Color(0xE6, 0x3E, 0x7A);
    private static final Color GRAY  = new Color(0xD9, 0xD9, 0xD9);
    private static final Color LGRAY = new Color(0xF2, 0xF2, 0xF2);
    private static final Color DARK  = new Color(0x22, 0x22, 0x22);
    private static final Color WHITE = Color.WHITE;

    private static final DateTimeFormatter FMT_API     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] TABLA_HEADERS = {
            "RAZÓN SOCIAL", "DÍAS FINAL", "DOCUMENTO", "EMISIÓN", "VENCIMIENTO", "N° ÚNICO", "BANCO",
            "No vencido", "Entre 0 y 30", "Entre 31 y 45", "Entre 46 y 60",
            "Entre 61 y 90", "Entre 91 y 180", "Mayor a 180", "Total general"
    };
    private static final List<String> BUCKETS = List.of(
            "No Vencido", "0 - 30", "31 - 45", "46 - 60", "61 - 90", "91 - 180", "+ 180"
    );
    private static final String[] FILTROS = {
            "No vencido", "Entre 0 y 30", "Entre 31 y 45", "Entre 46 y 60",
            "Entre 61 y 90", "Entre 91 y 180", "Mayor a 180"
    };

    public byte[] build(List<FacturasPorCobrarClientResponse> datos) throws DocumentException, IOException {
        String consultor = datos.isEmpty() ? "" : datos.get(0).vendedor();
        byte[] top10 = graficos.top10Png(datos);
        byte[] dona  = graficos.donaPng(datos);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(doc, out);
        doc.open();

        escribirCabecera(doc, consultor, top10);   // PASO 2 + TOP 10
        escribirFiltrosYDona(doc, dona);           // PASO 4 (dona) + PASO 5 (botones)
        escribirTabla(doc, datos);                 // PASO 3

        doc.close();
        return out.toByteArray();
    }

    private void escribirCabecera(Document doc, String consultor, byte[] top10Bytes)
            throws DocumentException, IOException {
        PdfPTable header = new PdfPTable(new float[]{70, 30});
        header.setWidthPercentage(100);

        PdfPTable info = new PdfPTable(new float[]{60, 40});
        info.setWidthPercentage(100);
        PdfPCell titulo = celda("Estado de cuenta", font(22, true, WHITE), NAVY, Element.ALIGN_LEFT);
        titulo.setColspan(2);
        titulo.setPaddingBottom(12);
        info.addCell(titulo);
        info.addCell(celda("Consultor: " + consultor, font(11, true, WHITE), PINK, Element.ALIGN_LEFT));
        info.addCell(celda(mesActual(), font(11, true, WHITE), PINK, Element.ALIGN_CENTER));

        PdfPCell left = new PdfPCell(info);
        left.setBackgroundColor(NAVY);
        left.setBorder(Rectangle.NO_BORDER);
        left.setPadding(12);
        header.addCell(left);

        Image top10 = Image.getInstance(top10Bytes);
        PdfPCell right = new PdfPCell(top10, true);
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_CENTER);
        right.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(right);

        doc.add(header);
    }

    private void escribirFiltrosYDona(Document doc, byte[] donaBytes) throws DocumentException, IOException {
        PdfPTable band = new PdfPTable(new float[]{55, 45});
        band.setWidthPercentage(100);
        band.setSpacingBefore(6);

        PdfPTable filtros = new PdfPTable(2);
        filtros.setWidthPercentage(100);
        PdfPCell rot = celda("\u25BE  Filtro", font(11, true, DARK), LGRAY, Element.ALIGN_LEFT);
        rot.setColspan(2);
        filtros.addCell(rot);
        for (String f : FILTROS) filtros.addCell(botonFiltro(f));
        if (FILTROS.length % 2 != 0) {
            PdfPCell vacio = new PdfPCell(new Phrase(""));
            vacio.setBackgroundColor(LGRAY);
            vacio.setBorder(Rectangle.NO_BORDER);
            filtros.addCell(vacio);
        }
        PdfPCell left = new PdfPCell(filtros);
        left.setBackgroundColor(LGRAY);
        left.setBorder(Rectangle.NO_BORDER);
        left.setPadding(8);
        band.addCell(left);

        Image dona = Image.getInstance(donaBytes);
        PdfPCell right = new PdfPCell(dona, true);
        right.setBackgroundColor(LGRAY);
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_CENTER);
        right.setVerticalAlignment(Element.ALIGN_MIDDLE);
        band.addCell(right);

        doc.add(band);
    }

    private PdfPCell botonFiltro(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font(9, true, WHITE)));
        c.setBackgroundColor(PINK);
        c.setBorder(Rectangle.BOX);
        c.setBorderColor(LGRAY);
        c.setBorderWidth(2);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(6);
        return c;
    }

    private void escribirTabla(Document doc, List<FacturasPorCobrarClientResponse> datos) throws DocumentException {
        PdfPTable table = new PdfPTable(15);
        table.setWidthPercentage(100);
        table.setSpacingBefore(8);
        table.setWidths(new float[]{24, 7, 10, 9, 10, 9, 10, 9, 9, 9, 9, 9, 9, 9, 11});

        Font hFont = font(7, true, WHITE);
        for (String h : TABLA_HEADERS) {
            PdfPCell c = new PdfPCell(new Phrase(h, hFont));
            c.setBackgroundColor(PINK);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setVerticalAlignment(Element.ALIGN_MIDDLE);
            c.setPadding(3);
            table.addCell(c);
        }

        Map<String, List<FacturasPorCobrarClientResponse>> porCliente = datos.stream()
                .collect(Collectors.groupingBy(FacturasPorCobrarClientResponse::nombre,
                        LinkedHashMap::new, Collectors.toList()));

        Font dFont = font(7, false, DARK);
        BigDecimal[] granTotal = nuevoAcumulador();

        for (Map.Entry<String, List<FacturasPorCobrarClientResponse>> e : porCliente.entrySet()) {
            String cliente = e.getKey();
            BigDecimal[] subtotal = nuevoAcumulador();

            for (FacturasPorCobrarClientResponse f : e.getValue()) {
                long dias = graficos.calcularDias(f.vencimiento());
                int idx = BUCKETS.indexOf(graficos.estadoVence(dias));
                BigDecimal saldo = f.saldo() != null ? f.saldo() : BigDecimal.ZERO;

                celdaTxt(table, cliente, dFont, Element.ALIGN_LEFT);
                celdaTxt(table, String.valueOf(dias), dFont, Element.ALIGN_CENTER);
                celdaTxt(table, f.comprobante(), dFont, Element.ALIGN_LEFT);
                celdaTxt(table, fecha(f.emision()), dFont, Element.ALIGN_CENTER);
                celdaTxt(table, fecha(f.vencimiento()), dFont, Element.ALIGN_CENTER);
                celdaTxt(table, "", dFont, Element.ALIGN_LEFT);   // N° Único
                celdaTxt(table, "", dFont, Element.ALIGN_LEFT);   // Banco
                for (int b = 0; b < 7; b++) celdaNum(table, b == idx ? saldo : null, dFont);
                celdaNum(table, saldo, dFont);

                subtotal[idx] = subtotal[idx].add(saldo);
                subtotal[7] = subtotal[7].add(saldo);
            }
            filaTotal(table, "Total " + cliente, subtotal);
            for (int b = 0; b < 8; b++) granTotal[b] = granTotal[b].add(subtotal[b]);
        }
        filaTotal(table, "TOTAL GENERAL", granTotal);

        doc.add(table);
    }

    private void celdaTxt(PdfPTable t, String v, Font f, int align) {
        PdfPCell c = new PdfPCell(new Phrase(v != null ? v : "", f));
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(3);
        t.addCell(c);
    }

    private void celdaNum(PdfPTable t, BigDecimal v, Font f) {
        String s = (v != null && v.signum() != 0) ? money(v) : "";
        PdfPCell c = new PdfPCell(new Phrase(s, f));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(3);
        t.addCell(c);
    }

    private void filaTotal(PdfPTable t, String rotulo, BigDecimal[] acum) {
        Font f = font(7, true, DARK);
        PdfPCell rot = new PdfPCell(new Phrase(rotulo, f));
        rot.setColspan(7);
        rot.setBackgroundColor(GRAY);
        rot.setHorizontalAlignment(Element.ALIGN_LEFT);
        rot.setPadding(3);
        t.addCell(rot);
        for (int b = 0; b < 7; b++) {
            PdfPCell c = new PdfPCell(new Phrase(acum[b].signum() != 0 ? money(acum[b]) : "", f));
            c.setBackgroundColor(GRAY);
            c.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c.setPadding(3);
            t.addCell(c);
        }
        PdfPCell tot = new PdfPCell(new Phrase(money(acum[7]), f));
        tot.setBackgroundColor(GRAY);
        tot.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tot.setPadding(3);
        t.addCell(tot);
    }

    private BigDecimal[] nuevoAcumulador() {
        BigDecimal[] a = new BigDecimal[8];
        Arrays.fill(a, BigDecimal.ZERO);
        return a;
    }

    private String money(BigDecimal v) { return new DecimalFormat("#,##0.00").format(v); }
    private String fecha(String v) { return LocalDate.parse(v, FMT_API).format(FMT_DISPLAY); }

    private String mesActual() {
        String m = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.of("es", "ES")));
        return Character.toUpperCase(m.charAt(0)) + m.substring(1);
    }

    private Font font(int size, boolean bold, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, bold ? Font.BOLD : Font.NORMAL, color);
    }

    private PdfPCell celda(String texto, Font f, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        if (bg != null) c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(6);
        return c;
    }
}