package br.com.sisaudcon.projeto.SAAMCND.dto;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CndClienteResponseDTO {

    private Long id;
    private String cnpj;
    private Integer periodicidade;
    private String statusCliente;
    private Boolean nacional;
    private Boolean municipal;
    private Boolean estadual;
    private Long fkEmpresa; // ID da empresa
    private String nomeEmpresa; // Nome da empresa para conveniência
    private String linha;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public CndClienteResponseDTO(CndCliente cliente) {
        this.id = cliente.getId();
        this.cnpj = cliente.getCnpj();
        this.periodicidade = cliente.getPeriodicidade();
        this.statusCliente = cliente.getStatusCliente();
        this.nacional = cliente.getNacional();
        this.municipal = cliente.getMunicipal();
        this.estadual = cliente.getEstadual();
        if (cliente.getEmpresa() != null) {
            this.fkEmpresa = cliente.getEmpresa().getId();
            this.nomeEmpresa = cliente.getEmpresa().getNomeEmpresa();
        }
        this.linha = cliente.getLinha();
        this.dataCadastro = cliente.getDataCadastro();
        this.dataAtualizacao = cliente.getDataAtualizacao();
    }
}
