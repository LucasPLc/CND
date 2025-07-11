package br.com.sisaudcon.projeto.SAAMCND.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cnd_empresa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CndEmpresa {

    @Id
    // O ID da empresa virá do SAAM, então não será gerado automaticamente pela nossa aplicação CND.
    // Se este 'id' é um ID interno do SAAM-CND para a tabela cnd_empresa, então GenerationType.IDENTITY seria apropriado.
    // Mas PEC-4924 diz: "Ao realizar o cadastro de um cliente, é obrigatório informar o campo fk_empresa que vincula o cliente a uma empresa previamente cadastrada no SAAM."
    // "A API deve obter automaticamente os dados da empresa (tabela cnd_empresa) do SAAM no momento do primeiro relacionamento"
    // Isso sugere que o ID da empresa é o ID do SAAM.
    // Vamos assumir que o ID é fornecido externamente (do SAAM) e não gerado aqui.
    // Se for um ID interno da tabela cnd_empresa, e o fk_empresa for o ID do SAAM, precisaremos de dois campos.
    // Por enquanto, vou manter como Long e não gerado, assumindo que é o ID do SAAM.
    private Long id;

    @NotBlank(message = "CNPJ da empresa é obrigatório")
    @Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Size(max = 255, message = "Nome da empresa deve ter no máximo 255 caracteres")
    @Column(name = "nome_empresa", length = 255)
    private String nomeEmpresa;

    // Este campo parece redundante se o 'id' da entidade CndEmpresa já é o 'id_empresa' do SAAM.
    // Vou remover 'idEmpresaSaam' e considerar que 'id' é o identificador único vindo do SAAM.
    // @Column(name = "id_empresa_saam", length = 6)
    // private String idEmpresaSaam;

    @Size(max = 50, message = "Status da empresa deve ter no máximo 50 caracteres")
    @Column(name = "status_empresa", length = 50)
    private String statusEmpresa;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.REFRESH, orphanRemoval = false) // Evitar cascade ALL para não deletar clientes ao deletar empresa, por exemplo.
    private List<CndCliente> clientes;

    @Column(name = "linha", length = 255)
    private String linha; // Campo de auditoria

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
