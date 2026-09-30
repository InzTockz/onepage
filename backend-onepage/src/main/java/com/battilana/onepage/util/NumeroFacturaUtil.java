package com.battilana.onepage.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class NumeroFacturaUtil {

    private NumeroFacturaUtil() {
    }

    public static String normalizar(String valor) {
        if (valor == null) return "";

        String s = valor.trim().toUpperCase();
        if (s.startsWith("FN-")) s = s.substring(3);
        else if (s.startsWith("LE-")) s = s.substring(3);

        String[] partes = s.split("-");
        if (partes.length == 2) {
            String alfa = partes[0];
            String numero = partes[1].replaceFirst("^0+", "");
            return alfa + numero;
        }
        return s.replaceFirst("^0+", "");
    }

    public static String resolverComprobanteSap(
            String nroExcel,
            String aceptante,
            BigDecimal importe,
            Map<String, String> sapPorNumero,
            Map<String, String> sapPorNombreImporte) {

        //1
        if(nroExcel != null) {
            String hit = sapPorNumero.get(normalizar(nroExcel));
            if(hit != null) return hit;
        }

        //2
        if (aceptante != null && importe != null){
            String clave = normalizarNombre(aceptante) + "|" + importe.setScale(2, RoundingMode.HALF_UP);
            String hit = sapPorNombreImporte.get(clave);
            if(hit!=null) return hit;
        }

        return nroExcel;
    }

    public static String normalizarNombre(String nombre) {
        if (nombre == null) return "";
        return nombre.trim().toUpperCase().replaceAll("\\s+", " ");
    }


}
