package br.com.fiap.hc.model.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class ConsultaRequest {

    private Date dataHora;

    private String status;

    private String areaMedica;

    private int idPaciente;

    private int idMedico;

}
