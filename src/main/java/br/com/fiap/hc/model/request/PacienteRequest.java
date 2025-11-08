package br.com.fiap.hc.model.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter

public class PacienteRequest {

    private int idPaciente;

    private String nome;

    private String cpf;

    private Date dataNascimento;

    private String telefone;

    private String email;

    private int idEndereco;

}
