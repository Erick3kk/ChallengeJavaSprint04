package br.com.fiap.hc.utils;

import java.sql.Date;

public class Metodos {

    public static String formatDataHora(Date data) {

        String dataHora = data.toString().replace('T', ' ');
        return dataHora;
    }
}
