package br.com.fiap.hc.model.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter

public class ReceitaRequest {


    private String medicamento;

    private String dosagem;

    private int idConsulta;


}
