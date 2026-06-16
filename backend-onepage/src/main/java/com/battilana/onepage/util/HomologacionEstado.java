package com.battilana.onepage.util;

import java.util.Map;

public class HomologacionEstado {

    private static final Map<String, String> MAPA = Map.ofEntries(
            // BBVA
            Map.entry("CAN", "CANCELADO"),
            Map.entry("ING", "INGRESADO"),
            Map.entry("DEV", "DEVUELTO"),
            Map.entry("DVP", "PROTESTADO"),
            Map.entry("DEE", "DESCARGO_ERROR"),
            Map.entry("INE", "INGRESO_ERROR"),
            Map.entry("REN", "RENOVADO"),
            Map.entry("RRI", "RENOVADO"),
            // BCP
            Map.entry("Cancelado", "CANCELADO"),
            Map.entry("Amortización", "AMORTIZADO"),
            Map.entry("Protestado", "PROTESTADO"),
            Map.entry("Devuelto", "DEVUELTO"),
            // Scotiabank
            Map.entry("Cancelacion", "CANCELADO"),
            Map.entry("Devolucion", "DEVUELTO"),
            Map.entry("Protesto", "PROTESTADO")
    );

    public static String homologar(String estadoOriginal) {
        if (estadoOriginal == null || estadoOriginal.isEmpty()) return "DESCONOCIDO";
        return MAPA.getOrDefault(estadoOriginal.trim(), "DESCONOCIDO");
    }
}
