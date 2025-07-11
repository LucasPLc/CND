package br.com.sisaudcon.projeto.SAAMCND.controller;

import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteRequestDTO;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteResponseDTO;
import br.com.sisaudcon.projeto.SAAMCND.service.CndClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de Clientes")
public class CndClienteController {

    private final CndClienteService cndClienteService;

    @Autowired
    public CndClienteController(CndClienteService cndClienteService) {
        this.cndClienteService = cndClienteService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente")
    public ResponseEntity<CndClienteResponseDTO> criarCliente(@Valid @RequestBody CndClienteRequestDTO requestDTO) {
        CndClienteResponseDTO novoCliente = cndClienteService.criarCliente(requestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoCliente.getId()).toUri();
        return ResponseEntity.created(location).body(novoCliente);
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes")
    public ResponseEntity<List<CndClienteResponseDTO>> listarClientes() {
        List<CndClienteResponseDTO> clientes = cndClienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente por ID")
    public ResponseEntity<CndClienteResponseDTO> buscarClientePorId(@PathVariable Long id) {
        CndClienteResponseDTO cliente = cndClienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente existente")
    public ResponseEntity<CndClienteResponseDTO> atualizarCliente(@PathVariable Long id, @Valid @RequestBody CndClienteRequestDTO requestDTO) {
        CndClienteResponseDTO clienteAtualizado = cndClienteService.atualizarCliente(id, requestDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um cliente por ID")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        cndClienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
