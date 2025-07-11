package br.com.sisaudcon.projeto.SAAMCND.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate; // Para data_emissao e data_validade
import java.time.LocalDateTime;

@Entity
@Table(name = "cnd_resultado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CndResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_processamento")
    private LocalDateTime dataProcessamento;

    @Lob
    @Column(name = "arquivo")
    private byte[] arquivo; // Conteúdo binário do PDF

    @Column(length = 100) // Aumentado para acomodar descrições mais longas
    private String situacao; // Ex: "Positiva com efeitos de negativa", "Negativa de Débitos"

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "codigo_controle", length = 100)
    private String codigoControle;

    @Column(name = "status_processamento", length = 50) // Ex: "concluido", "erro_extracao", "pendente_extracao"
    private String statusProcessamento; // Diferente de 'status' da CND em si. Este é o status da nossa operação interna.

    @Column(name = "mensagem_erro_processamento", length = 500)
    private String mensagemErroProcessamento;

    @Column(name = "tipo_certidao", length = 50) // Ex: Federal, Estadual, Municipal
    private String tipoCertidao;

    @Column(name = "orgao_emissor", length = 100) // Ex: Receita Federal, SEFAZ-SP, Prefeitura XYZ
    private String orgaoEmissor;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_cliente", nullable = false)
    private CndCliente cliente;

    @Column(name = "linha", length = 255)
    private String linha;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (dataProcessamento == null) {
            dataProcessamento = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
