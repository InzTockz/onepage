package com.battilana.onepage.util.parser;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Carga un archivo de banco como un {@link Workbook} de POI, sin importar si el
 * contenido real es un Excel binario (.xls), un Excel moderno (.xlsx) o una
 * tabla HTML disfrazada de .xls. El resto del sistema solo ve un Workbook.
 */

public class WorkbookLoader {

    private WorkbookLoader() {
    } // clase de utilidad: no se instancia

    public static Workbook cargar(MultipartFile archivo) throws IOException {
        byte[] bytes = archivo.getBytes();

        if (esHtml(bytes)) {
            return HtmlWorkbookBuilder.construir(bytes);
        }
        return WorkbookFactory.create(new ByteArrayInputStream(bytes));
    }

    /**
     * Husmea los primeros bytes: ¿el contenido real es una tabla HTML?
     */
    private static boolean esHtml(byte[] bytes) {
        int n = Math.min(bytes.length, 512);
        String inicio = new String(bytes, 0, n, StandardCharsets.ISO_8859_1)
                .toLowerCase();
        return inicio.contains("<html")
                || inicio.contains("<table")
                || inicio.contains("!doctype")
                || inicio.contains("<tr");
    }
}