package br.com.fiap.hc.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class LoginRequest {

    private String email;

    private String cpf;
}
