package br.com.scardtopia.gestao.repository;

import br.com.scardtopia.gestao.model.Produto;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByOrderByNomeAsc();
    List<Produto> findByNomeContainingIgnoreCaseOrderByNomeAsc(String busca);
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    long countByEstoqueLessThanEqual(int limite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Produto p WHERE p.id = :id")
    Optional<Produto> buscarParaAtualizar(@Param("id") Long id);
}
