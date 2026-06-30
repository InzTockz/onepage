package com.battilana.onepage.util;

import java.util.Map;

public class HomologacionEstadoVigente {

    private static final Map<String, String> MAPA = Map.ofEntries(
            Map.entry("VIG", "VIGNETE"),
            Map.entry("VIGENTE", "VIGENTE"),
            Map.entry("Vigente", "VIGENTE")
    );

    public static String homologar(String estadoOriginal){
        if(estadoOriginal == null || estadoOriginal.isBlank()) return "VIGENTE";
        return MAPA.getOrDefault(estadoOriginal.trim(), "VIGENTE");
    }
}
