package com.battilana.onepage.dto.reportes;

public record ReportConfig(
        boolean mostrarCabecera,
        String nombreArchivo
) {

    //Factories estaticas para los tipos de reporte que ya tienes
    public static ReportConfig porVendedor(){
        return new ReportConfig(true, "reporte_vendedor");
    }

    public static ReportConfig general(){
        return new ReportConfig(false, "reporte_general");
    }
}
