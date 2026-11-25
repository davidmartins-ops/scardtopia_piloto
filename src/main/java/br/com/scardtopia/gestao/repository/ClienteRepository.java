package br.com.scardtopia.gestao.repository;

import br.com.scardtopia.gestao.model.Cliente;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findAllByOrderByNomeAsc();
    List<Cliente> findByNomeContainingIgnoreCaseOrderByNomeAsc(String busca);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
