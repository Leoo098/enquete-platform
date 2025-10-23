package com.project.enquete.core.enquete_platform.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordForm {

    @NotBlank(message = "Campo obrigatório")
//    @Size(min = 8)
    private String password;

    @NotBlank(message = "Campo obrigatório")
    private String passwordConfirmation;
}
