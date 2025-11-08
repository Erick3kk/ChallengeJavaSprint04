package br.com.fiap.hc.dto.consulta;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;


import java.util.Date;

@Getter
@Setter

public class AtualizarConsultaDto {

    private int idConsulta;

    @NotBlank
    private String status;

}
