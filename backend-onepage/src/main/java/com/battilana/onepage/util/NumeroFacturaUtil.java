package com.battilana.onepage.util;

public class NumeroFacturaUtil {

    private NumeroFacturaUtil(){}

    public static String normalizar(String valor){
        if(valor == null) return "";

        String s = valor.trim().toUpperCase();
        if(s.startsWith("FN-")) s = s.substring(3);
        else if (s.startsWith("LE-")) s = s.substring(3);

        String[] partes = s.split("-");
        if(partes.length == 2){
            String alfa = partes[0];
            String numero = partes[1].replaceFirst("^0+", "");
            return alfa + numero;
        }
        return s.replaceFirst("^0+", "");
    }
}
