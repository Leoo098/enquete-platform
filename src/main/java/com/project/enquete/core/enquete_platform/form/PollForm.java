package com.project.enquete.core.enquete_platform.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PollForm {

    @NotBlank(message = "A pergunta é obrigatória")
    private String question;

    @Min(value = 1, message = "A duração deve ser maior que 0")
    private int duration;

    @NotBlank(message = "A unidade de tempo é obrigatória")
    private String timeUnit;

    @Valid
    @Size(min = 2, message = "Deve ter pelo menos 2 opções")
    private List<OptionForm> options = Arrays.asList(new OptionForm(), new OptionForm());

}
