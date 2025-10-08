package com.project.enquete.core.enquete_platform.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 20, message = "O nome de usuário deve ter entre 3 e 20 caracteres")
    private String username;

    @NotBlank(message = "Campo obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Campo obrigatório")
    private String password;
}
