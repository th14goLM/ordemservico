package com.exemplo.ordemservico.repository;

import com.exemplo.ordemservico.enums.Prioridade;
import com.exemplo.ordemservico.enums.StatusOS;
import com.exemplo.ordemservico.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    // SELECT * FROM ordens_servico WHERE status = ?
    List<OrdemServico> findByStatus(StatusOS status);

    // SELECT * FROM ordens_servico WHERE cliente_id = ?
    List<OrdemServico> findByClienteId(Long clienteId);

    // EXERCÍCIO (consulta): SELECT * FROM ordens_servico WHERE prioridade = ?
    List<OrdemServico> findByPrioridade(Prioridade prioridade);

    // EXERCÍCIO (usado pela Regra 9): existe alguma OS para este cliente?
    boolean existsByClienteId(Long clienteId);
}
