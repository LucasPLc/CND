package br.com.sisaudcon.projeto.SAAMCND.repository;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CndClienteRepository extends JpaRepository<CndCliente, Long> {
    Optional<CndCliente> findByCnpjAndEmpresaId(String cnpj, Long empresaId);
    List<CndCliente> findByEmpresaId(Long empresaId);

    // Para verificar se existem resultados vinculados antes de excluir um cliente
    @Query("SELECT COUNT(cr) > 0 FROM CndResultado cr WHERE cr.cliente.id = :clienteId")
    boolean existsCndResultadoByClienteId(@Param("clienteId") Long clienteId);
}
