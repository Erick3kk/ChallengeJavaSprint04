package br.com.fiap.hc.dto.paciente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginPacienteDto {

    @Size(max = 12)
    private String cpf;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter formato valido")
    private String email;
}
