package br.com.sisaudcon.projeto.SAAMCND.repository;

import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CndResultadoRepository extends JpaRepository<CndResultado, Long>, JpaSpecificationExecutor<CndResultado> {
    List<CndResultado> findByClienteId(Long clienteId);

    // Para PEC-4963: buscar registros com status = 'concluido' e situacao IS NULL
    // O status 'concluido' aqui refere-se ao status do processamento da consulta da CND, não à situação da CND (negativa/positiva).
    // Vamos usar o campo 'statusProcessamento' para isso.
    List<CndResultado> findByStatusProcessamentoAndSituacaoIsNull(String statusProcessamento);

    // Se 'status' na PEC-4963 se refere a um campo genérico de status da entidade CndResultado,
    // e não ao 'statusProcessamento' que adicionei, precisaria de um campo 'status' na entidade.
    // Por enquanto, interpretei como 'statusProcessamento'.
}
