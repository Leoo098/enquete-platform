package com.project.enquete.core.enquete_platform.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PollForm {

    @NotBlank(message = "A pergunta é obrigatória")
    private String question;

    @Min(value = 1, message = "A duração deve ser maior que 0")
    private int duration;

    @NotBlank(message = "A unidade de tempo é obrigatória")
    private String timeUnit;

    @Size(min = 2)
    private List<OptionForm> options;

    @NotBlank
    private String visibility;

    public PollForm(){
        this.options = new ArrayList<>();
        this.options.add(new OptionForm());
        this.options.add(new OptionForm());
    }
}
