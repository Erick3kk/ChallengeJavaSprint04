package br.com.fiap.hc.dto.paciente;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter

public class CadastroPacienteDto {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, min = 2)
    private String nome;

    @Size(max = 12)
    private String cpf;

    @Past(message = "A data deve estar no passado")
    private Date dataNascimento;

    @NotBlank
    @Size(max = 20)
    private String telefone;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter formato valido")
    private String email;

    private int idEndereco;

}
