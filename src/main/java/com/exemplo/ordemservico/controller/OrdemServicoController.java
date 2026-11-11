package com.exemplo.ordemservico.controller;

import com.exemplo.ordemservico.enums.Prioridade;
import com.exemplo.ordemservico.enums.StatusOS;
import com.exemplo.ordemservico.model.OrdemServico;
import com.exemplo.ordemservico.service.OrdemServicoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/ordens")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    // GET /ordens ou GET /ordens?status=ABERTA
    @GetMapping
    public List<OrdemServico> listar(
            @RequestParam(required = false) StatusOS status) {
        if (status != null) {
            return ordemServicoService.listarPorStatus(status);
        }
        return ordemServicoService.listarTodas();
    }

    // GET /ordens/1
    @GetMapping("/{id}")
    public OrdemServico buscar(@PathVariable Long id) {
        return ordemServicoService.buscarPorId(id);
    }

    // GET /ordens/cliente/1
    @GetMapping("/cliente/{clienteId}")
    public List<OrdemServico> listarPorCliente(@PathVariable Long clienteId) {
        return ordemServicoService.listarPorCliente(clienteId);
    }

    // EXERCÍCIO: GET /ordens/prioridade/ALTA
    @GetMapping("/prioridade/{prioridade}")
    public List<OrdemServico> listarPorPrioridade(@PathVariable Prioridade prioridade) {
        return ordemServicoService.listarPorPrioridade(prioridade);
    }

    // POST /ordens
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdemServico abrir(@RequestBody OrdemServico os) {
        return ordemServicoService.abrir(os);
    }

    // PUT /ordens/1/tecnico/2
    @PutMapping("/{id}/tecnico/{tecnicoId}")
    public OrdemServico atribuirTecnico(@PathVariable Long id,
                                        @PathVariable Long tecnicoId) {
        return ordemServicoService.atribuirTecnico(id, tecnicoId);
    }

    // PUT /ordens/1/concluir?valor=150.00
    @PutMapping("/{id}/concluir")
    public OrdemServico concluir(@PathVariable Long id,
                                 @RequestParam(required = false) BigDecimal valor) {
        return ordemServicoService.concluir(id, valor);
    }

    // PUT /ordens/1/cancelar
    @PutMapping("/{id}/cancelar")
    public OrdemServico cancelar(@PathVariable Long id) {
        return ordemServicoService.cancelar(id);
    }
}
