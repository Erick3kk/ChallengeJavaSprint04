package br.com.fiap.hc.model.response;

import br.com.fiap.hc.model.Endereco;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class LoginResponse {

    private int idPaciente;

    private String nome;

    private String cpf;

    private String dataNascimento;

    private String telefone;

    private String email;

    private Endereco endereco;


}
