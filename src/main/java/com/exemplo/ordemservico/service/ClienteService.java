package com.exemplo.ordemservico.service;

import com.exemplo.ordemservico.model.Cliente;
import com.exemplo.ordemservico.repository.ClienteRepository;
import com.exemplo.ordemservico.repository.OrdemServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    // usado pela Regra 9 (exercício). É o repository e não o OrdemServicoService
    // porque o OrdemServicoService já depende do ClienteService (evita dependência circular).
    private final OrdemServicoRepository ordemServicoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          OrdemServicoRepository ordemServicoRepository) {
        this.clienteRepository = clienteRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id));
    }

    // REGRA 1: não pode existir dois clientes com o mesmo CPF
    public Cliente salvar(Cliente cliente) {
        cliente.setId(null); // o id é gerado pelo banco; ignora id enviado no JSON
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Já existe cliente com este CPF");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dados.getNome());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());
        return clienteRepository.save(cliente);
    }

    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        validarClienteSemOrdens(cliente); // usa a Regra 9
        clienteRepository.delete(cliente);
    }

    // REGRA 9: um cliente com OS cadastrada não pode ser excluído
    private void validarClienteSemOrdens(Cliente cliente) {
        if (ordemServicoRepository.existsByClienteId(cliente.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O cliente " + cliente.getId() + " possui ordens de serviço e não pode ser excluído");
        }
    }
}
