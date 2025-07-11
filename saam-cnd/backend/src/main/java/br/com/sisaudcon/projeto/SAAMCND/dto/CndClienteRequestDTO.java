package br.com.sisaudcon.projeto.SAAMCND.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CndClienteRequestDTO {

    @NotBlank(message = "CNPJ do cliente é obrigatório")
    @Size(min = 14, max = 18, message = "CNPJ deve ter entre 14 e 18 caracteres")
    private String cnpj;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(max = 255, message = "Nome do cliente deve ter no máximo 255 caracteres")
    private String nome;

    @NotNull(message = "Periodicidade é obrigatória")
    @Positive(message = "Periodicidade deve ser um valor inteiro positivo")
    private Integer periodicidade;

    @NotBlank(message = "Status do cliente é obrigatório")
    @Size(max = 50, message = "Status do cliente deve ter no máximo 50 caracteres")
    private String statusCliente;

    @NotNull(message = "Campo 'nacional' é obrigatório")
    private Boolean nacional;

    @NotNull(message = "Campo 'municipal' é obrigatório")
    private Boolean municipal;

    @NotNull(message = "Campo 'estadual' é obrigatório")
    private Boolean estadual;

    @NotNull(message = "ID da Empresa (fk_empresa) é obrigatório")
    private Long fkEmpresa; // ID da empresa a qual o cliente pertence
}
