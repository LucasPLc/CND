package br.com.sisaudcon.projeto.SAAMCND.repository;

import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CndEmpresaRepository extends JpaRepository<CndEmpresa, Long> {
    Optional<CndEmpresa> findByCnpj(String cnpj);
    // O ID da empresa é o ID do SAAM, que já é o @Id da entidade.
    // findById já é fornecido pelo JpaRepository.
}
