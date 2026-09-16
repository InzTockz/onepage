package com.battilana.onepage.util.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Convierte una tabla HTML (el "Excel trampa") en un {@link Workbook} de POI,
 * de modo que los parsers existentes funcionen sin cambios.
 * Todas las celdas se crean como texto (STRING): CeldaUtil ya sabe convertir
 * texto a número/fecha/decimal cuando lo necesita.
 */
public class HtmlWorkbookBuilder {

    private HtmlWorkbookBuilder() {}

    public static Workbook construir(byte[] bytes) throws IOException {
        // jsoup detecta el charset del <meta>; si no hay, usa UTF-8
        Document doc = Jsoup.parse(new ByteArrayInputStream(bytes), "windows-1252", "");

        Workbook workbook = new XSSFWorkbook();
        Sheet hoja = workbook.createSheet("Hoja1");

        Elements filasHtml = doc.select("tr"); // todas las filas de todas las tablas, en orden
        int numFila = 0;

        for (Element trHtml : filasHtml) {
            Row fila = hoja.createRow(numFila++);
            Elements celdasHtml = trHtml.select("td, th");
            int numCol = 0;
            for (Element celdaHtml : celdasHtml) {
                Cell celda = fila.createCell(numCol++);
                // .text() ya decodifica &nbsp;, &amp;, etc. y colapsa espacios
                celda.setCellValue(celdaHtml.text().trim());
            }
        }
        return workbook;
    }
}
