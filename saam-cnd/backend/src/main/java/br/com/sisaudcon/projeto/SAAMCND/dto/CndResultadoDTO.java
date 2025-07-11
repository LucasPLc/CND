package br.com.sisaudcon.projeto.SAAMCND.dto;

import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64; // Importar Base64

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Não incluir campos nulos na serialização JSON
public class CndResultadoDTO {

    private Long id;
    private LocalDateTime dataProcessamento;
    private String arquivo; // Manter como String (Base64) no DTO para tráfego JSON
    private String situacao;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private String codigoControle;
    private String statusProcessamento;
    private String mensagemErroProcessamento;
    private String tipoCertidao;
    private String orgaoEmissor;
    private String observacoes;
    private Long fkCliente;
    private String cnpjCliente; // Para facilitar a identificação no DTO
    private String linha;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    // Construtor para mapear da Entidade para DTO
    public CndResultadoDTO(CndResultado resultado) {
        this.id = resultado.getId();
        this.dataProcessamento = resultado.getDataProcessamento();
        if (resultado.getArquivo() != null && resultado.getArquivo().length > 0) {
            this.arquivo = Base64.getEncoder().encodeToString(resultado.getArquivo());
        } else {
            this.arquivo = null; // Ou String vazia, dependendo da preferência
        }
        this.situacao = resultado.getSituacao();
        this.dataEmissao = resultado.getDataEmissao();
        this.dataValidade = resultado.getDataValidade();
        this.codigoControle = resultado.getCodigoControle();
        this.statusProcessamento = resultado.getStatusProcessamento();
        this.mensagemErroProcessamento = resultado.getMensagemErroProcessamento();
        this.tipoCertidao = resultado.getTipoCertidao();
        this.orgaoEmissor = resultado.getOrgaoEmissor();
        this.observacoes = resultado.getObservacoes();
        if (resultado.getCliente() != null) {
            this.fkCliente = resultado.getCliente().getId();
            this.cnpjCliente = resultado.getCliente().getCnpj();
        }
        this.linha = resultado.getLinha();
        this.dataCadastro = resultado.getDataCadastro();
        this.dataAtualizacao = resultado.getDataAtualizacao();
    }

    // Método para converter DTO para Entidade (parcial, pois cliente é referenciado por ID)
    public CndResultado toEntity() {
        CndResultado resultado = new CndResultado();
        resultado.setId(this.id);
        resultado.setDataProcessamento(this.dataProcessamento);
        // O campo 'arquivo' (byte[]) é tratado no CndResultadoService durante a conversão DTO -> Entidade.
        // Não setamos aqui para evitar inconsistência de tipos (String DTO vs byte[] Entidade).
        resultado.setSituacao(this.situacao);
        resultado.setDataEmissao(this.dataEmissao);
        resultado.setDataValidade(this.dataValidade);
        resultado.setCodigoControle(this.codigoControle);
        resultado.setStatusProcessamento(this.statusProcessamento);
        resultado.setMensagemErroProcessamento(this.mensagemErroProcessamento);
        resultado.setTipoCertidao(this.tipoCertidao);
        resultado.setOrgaoEmissor(this.orgaoEmissor);
        resultado.setObservacoes(this.observacoes);
        resultado.setLinha(this.linha);
        // O cliente (fkCliente) deve ser tratado no serviço ao converter para entidade
        return resultado;
    }
}
