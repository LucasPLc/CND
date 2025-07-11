package br.com.sisaudcon.projeto.SAAMCND.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CndEmpresaDTO {

    private Long id; // ID da empresa (vindo do SAAM)

    @NotBlank(message = "CNPJ da empresa é obrigatório")
    @Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    private String cnpj;

    @Size(max = 255, message = "Nome da empresa deve ter no máximo 255 caracteres")
    private String nomeEmpresa;

    // O campo idEmpresaSaam foi removido da entidade, pois o 'id' já cumpre esse papel.
    // private String idEmpresaSaam;

    @Size(max = 50, message = "Status da empresa deve ter no máximo 50 caracteres")
    private String statusEmpresa;

    private String linha;


    // Construtor para mapear da Entidade para DTO
    public CndEmpresaDTO(CndEmpresa empresa) {
        this.id = empresa.getId();
        this.cnpj = empresa.getCnpj();
        this.nomeEmpresa = empresa.getNomeEmpresa();
        this.statusEmpresa = empresa.getStatusEmpresa();
        this.linha = empresa.getLinha();
    }

    // Método para converter DTO para Entidade (pode ser útil, mas geralmente é feito no serviço)
    public CndEmpresa toEntity() {
        CndEmpresa empresa = new CndEmpresa();
        empresa.setId(this.id); // Importante ao converter de DTO para entidade existente
        empresa.setCnpj(this.cnpj);
        empresa.setNomeEmpresa(this.nomeEmpresa);
        empresa.setStatusEmpresa(this.statusEmpresa);
        empresa.setLinha(this.linha);
        // Clientes não são gerenciados diretamente por este DTO
        return empresa;
    }
}
