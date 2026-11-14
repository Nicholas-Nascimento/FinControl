package br.com.fincontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatadorFinanceiro {

    public static String formatar(long centavos) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return nf.format(centavos / 100.0);
    }

    // "1234,56" ou "1234.56" -> 123456. Lança NumberFormatException se inválido.
    public static long paraCentavos(String texto) {
        String limpo = texto.trim().replace(",", ".");
        return new BigDecimal(limpo)
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    public static String hojeIso() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static String mesAtual() {
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
    }

    public static String dataParaExibicao(String iso) {
        String[] p = iso.split("-");
        return p[2] + "/" + p[1] + "/" + p[0];
    }
}