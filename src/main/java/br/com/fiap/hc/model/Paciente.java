package br.com.fiap.hc.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter

public class Paciente {

    private int idPaciente;
    private String nome;
    private String cpf;
    private Date dataNascimento;
    private String telefone;
    private String email;
    private Endereco endereco;

    public Paciente() {
    }

    public Paciente(int idPaciente, String nome, String cpf, Date dataNascimeto, String telefone, String email, Endereco endereco) {
        this.idPaciente = idPaciente;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimeto;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
    }

    public Paciente(int idPaciente) {
    }

    @Override
    public String toString() {
        return "idPaciente: " + idPaciente + ", Nome: " + nome + ", CPF: " +
                cpf + " Data de Nascimento: " + dataNascimento + ", Telefone: " + telefone + ", Email: " + email + "Endereco: " + endereco;
    }
}
