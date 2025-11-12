package com.project.enquete.core.enquete_platform.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordForm {

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Campo obrigatório")
    private String passwordConfirmation;
}
