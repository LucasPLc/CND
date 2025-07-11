package br.com.sisaudcon.projeto.SAAMCND.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cnd_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CndCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CNPJ do cliente é obrigatório")
    @Size(min = 14, max = 18, message = "CNPJ deve ter entre 14 e 18 caracteres") // XX.XXX.XXX/XXXX-XX (18) ou XXXXXXXXXXXXXX (14)
    @Column(nullable = false, length = 18) // Não pode ser unique globalmente se diferentes empresas podem ter clientes com mesmo CNPJ (improvável, mas considerar)
    private String cnpj;

    @NotNull(message = "Periodicidade é obrigatória")
    @Positive(message = "Periodicidade deve ser um valor positivo")
    @Column(nullable = false)
    private Integer periodicidade;

    @NotBlank(message = "Status do cliente é obrigatório")
    @Size(max = 50, message = "Status do cliente deve ter no máximo 50 caracteres")
    @Column(name = "status_cliente", nullable = false, length = 50)
    private String statusCliente;

    @NotNull(message = "Campo 'nacional' é obrigatório")
    @Column(nullable = false)
    private Boolean nacional;

    @NotNull(message = "Campo 'municipal' é obrigatório")
    @Column(nullable = false)
    private Boolean municipal;

    @NotNull(message = "Campo 'estadual' é obrigatório")
    @Column(nullable = false)
    private Boolean estadual;

    @NotNull(message = "Empresa (fk_empresa) é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_empresa", nullable = false)
    private CndEmpresa empresa;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CndResultado> resultados;

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
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
