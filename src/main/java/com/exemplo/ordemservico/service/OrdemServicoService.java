package com.exemplo.ordemservico.service;

import com.exemplo.ordemservico.enums.Prioridade;
import com.exemplo.ordemservico.enums.StatusOS;
import com.exemplo.ordemservico.model.Cliente;
import com.exemplo.ordemservico.model.OrdemServico;
import com.exemplo.ordemservico.model.Tecnico;
import com.exemplo.ordemservico.repository.OrdemServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteService clienteService;
    private final TecnicoService tecnicoService;

    public OrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ClienteService clienteService,
            TecnicoService tecnicoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteService = clienteService;
        this.tecnicoService = tecnicoService;
    }

    // CONSULTAS (sem regra de negócio)
    public List<OrdemServico> listarTodas() {
        return ordemServicoRepository.findAll();
    }

    public List<OrdemServico> listarPorStatus(StatusOS status) {
        return ordemServicoRepository.findByStatus(status);
    }

    public List<OrdemServico> listarPorCliente(Long clienteId) {
        return ordemServicoRepository.findByClienteId(clienteId);
    }

    // EXERCÍCIO (consulta): lista as OS de uma prioridade
    public List<OrdemServico> listarPorPrioridade(Prioridade prioridade) {
        return ordemServicoRepository.findByPrioridade(prioridade);
    }

    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ordem de serviço não encontrada: " + id));
    }

    // REGRAS DE NEGÓCIO

    // REGRA 2: a OS só pode ser aberta para um cliente cadastrado
    private Cliente validarClienteCadastrado(OrdemServico os) {
        if (os.getCliente() == null || os.getCliente().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe o id do cliente");
        }
        return clienteService.buscarPorId(os.getCliente().getId());
    }

    // REGRA 3: ao abrir, a OS recebe a data de abertura e o status ABERTA
    public OrdemServico abrir(OrdemServico os) {
        Cliente cliente = validarClienteCadastrado(os); // usa a Regra 2
        os.setId(null); // o id é gerado pelo banco; ignora id enviado no JSON
        os.setCliente(cliente);
        os.setTecnico(null);      // a OS nasce sem técnico (Regra 5 atribui depois)
        os.setValor(null);        // o valor só é definido ao concluir (Regra 7)
        os.setDataFechamento(null);
        os.setDataAbertura(LocalDateTime.now());
        os.setStatus(StatusOS.ABERTA);
        return ordemServicoRepository.save(os);
    }

    // REGRA 4: uma OS CONCLUIDA ou CANCELADA não pode mais ser alterada
    private void validarOsNaoFinalizada(OrdemServico os) {
        if (os.getStatus() == StatusOS.CONCLUIDA
                || os.getStatus() == StatusOS.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A OS " + os.getId() + " já está " + os.getStatus());
        }
    }

    // REGRA 5: ao atribuir um técnico, a OS passa para EM_ANDAMENTO
    public OrdemServico atribuirTecnico(Long osId, Long tecnicoId) {
        OrdemServico os = buscarPorId(osId);
        validarOsNaoFinalizada(os); // usa a Regra 4
        Tecnico tecnico = tecnicoService.buscarPorId(tecnicoId);
        os.setTecnico(tecnico);
        os.setStatus(StatusOS.EM_ANDAMENTO);
        return ordemServicoRepository.save(os);
    }

    // REGRA 6: só é possível concluir uma OS que tenha técnico
    private void validarTecnicoAtribuido(OrdemServico os) {
        if (os.getTecnico() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Atribua um técnico antes de concluir");
        }
    }

    // REGRA 10: o valor ao concluir uma OS deve ser maior que zero
    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O valor para concluir a OS deve ser maior que zero");
        }
    }

    // REGRA 7: ao concluir, a OS recebe valor,
    // data de fechamento e status CONCLUIDA
    public OrdemServico concluir(Long osId, BigDecimal valor) {
        OrdemServico os = buscarPorId(osId);
        validarOsNaoFinalizada(os);  // usa a Regra 4
        validarTecnicoAtribuido(os); // usa a Regra 6
        validarValorPositivo(valor); // usa a Regra 10
        os.setValor(valor);
        os.setDataFechamento(LocalDateTime.now());
        os.setStatus(StatusOS.CONCLUIDA);
        return ordemServicoRepository.save(os);
    }

    // REGRA 8: ao cancelar, a OS recebe data de fechamento
    // e status CANCELADA
    public OrdemServico cancelar(Long osId) {
        OrdemServico os = buscarPorId(osId);
        validarOsNaoFinalizada(os); // usa a Regra 4
        os.setDataFechamento(LocalDateTime.now());
        os.setStatus(StatusOS.CANCELADA);
        return ordemServicoRepository.save(os);
    }
}
