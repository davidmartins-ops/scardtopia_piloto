package br.com.scardtopia.gestao.repository;

import br.com.scardtopia.gestao.model.Venda;
import br.com.scardtopia.gestao.model.StatusVenda;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findAllByOrderByCriadoEmDesc();
    List<Venda> findTop5ByOrderByCriadoEmDesc();
    boolean existsByProdutoId(Long id);
    boolean existsByClienteId(Long id);
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venda v WHERE v.status = :status")
    BigDecimal somarTotalPorStatus(@Param("status") StatusVenda status);
}
