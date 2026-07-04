package com.battilana.onepage.util.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExcelStyles {

    public final CellStyle title;
    public final CellStyle header;
    public final CellStyle number;

    public ExcelStyles(Workbook wb){
        //titulo consultor
        title = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 12);
        title.setFont(titleFont);

        // Header (createCellStyle() ya es XSSFCellStyle, el cast es válido)
        XSSFCellStyle h = (XSSFCellStyle) wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        h.setFont(headerFont);
        h.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 26, (byte) 46, (byte) 113}, null));
        h.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        h.setAlignment(HorizontalAlignment.CENTER);
        h.setBorderBottom(BorderStyle.THIN);
        header = h;

        // Números
        number = wb.createCellStyle();
        DataFormat fmt = wb.createDataFormat();
        number.setDataFormat(fmt.getFormat("#,##0.00"));
        number.setAlignment(HorizontalAlignment.RIGHT);
    }
}
