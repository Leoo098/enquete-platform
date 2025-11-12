package com.project.enquete.core.enquete_platform.dto.form;

import jakarta.validation.constraints.AssertTrue;
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
    @Size(min = 6, max = 60, message = "Sua senha deve conter pelo menos 8 caracteres")
    private String password;

    @NotBlank(message = "Campo obrigatório")
    private String passwordConfirmation;

    @AssertTrue(message = "As senhas não coincidem")
    public boolean isPasswordsMatch() {
        if (password == null || passwordConfirmation == null) {
            return false;
        }
        return password.equals(passwordConfirmation);
    }
}
