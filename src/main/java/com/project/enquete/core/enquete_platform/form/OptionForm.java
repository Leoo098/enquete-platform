package com.project.enquete.core.enquete_platform.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionForm {

    @NotBlank(message = "O texto da opção é obrigatório")
    private String text;
}
