package com.project.enquete.core.enquete_platform.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionForm {

    @NotBlank(message = "O texto da opção é obrigatório")
    @Size(min = 1, max = 60)
    private String text;
}
