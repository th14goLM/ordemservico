package com.exemplo.ordemservico.controller;

import com.exemplo.ordemservico.model.Tecnico;
import com.exemplo.ordemservico.service.TecnicoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    // GET /tecnicos
    @GetMapping
    public List<Tecnico> listar() {
        return tecnicoService.listarTodos();
    }

    // GET /tecnicos/1
    @GetMapping("/{id}")
    public Tecnico buscar(@PathVariable Long id) {
        return tecnicoService.buscarPorId(id);
    }

    // POST /tecnicos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tecnico cadastrar(@RequestBody Tecnico tecnico) {
        return tecnicoService.salvar(tecnico);
    }

    // PUT /tecnicos/1
    @PutMapping("/{id}")
    public Tecnico atualizar(@PathVariable Long id,
                             @RequestBody Tecnico tecnico) {
        return tecnicoService.atualizar(id, tecnico);
    }

    // DELETE /tecnicos/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        tecnicoService.excluir(id);
    }
}
