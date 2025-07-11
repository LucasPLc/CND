package br.com.sisaudcon.projeto.SAAMCND.controller;

import br.com.sisaudcon.projeto.SAAMCND.dto.CndResultadoDTO;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.service.CndFederalService;
import br.com.sisaudcon.projeto.SAAMCND.service.CndResultadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cnd-resultados")
@Tag(name = "Resultados CND", description = "API para gerenciamento de Resultados de CND")
public class CndResultadoController {

    private final CndResultadoService cndResultadoService;
    private final CndFederalService cndFederalService; // Adicionado para PEC-4869

    @Autowired
    public CndResultadoController(CndResultadoService cndResultadoService, CndFederalService cndFederalService) {
        this.cndResultadoService = cndResultadoService;
        this.cndFederalService = cndFederalService;
    }

    @PostMapping("/consulta-federal/{clienteId}")
    @Operation(summary = "Dispara uma consulta simulada de CND Federal para um cliente (PEC-4869)")
    public ResponseEntity<CndResultadoDTO> consultarCndFederal(@PathVariable Long clienteId) {
        CndResultado resultado = cndFederalService.consultarCndFederalParaCliente(clienteId);
        return ResponseEntity.ok(new CndResultadoDTO(resultado));
    }

    @PostMapping
    @Operation(summary = "Cria um novo resultado de CND (geralmente usado internamente por outros serviços)")
    public ResponseEntity<CndResultadoDTO> criarCndResultado(@Valid @RequestBody CndResultadoDTO dto) {
        CndResultadoDTO novoResultado = cndResultadoService.criarCndResultado(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoResultado.getId()).toUri();
        return ResponseEntity.created(location).body(novoResultado);
    }

    @GetMapping
    @Operation(summary = "Lista todos os resultados de CND, com filtros opcionais")
    public ResponseEntity<List<CndResultadoDTO>> listarCndResultados(@RequestParam(required = false) Map<String, String> filters) {
        List<CndResultadoDTO> resultados = cndResultadoService.listarCndResultados(filters);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um resultado de CND por ID")
    public ResponseEntity<CndResultadoDTO> buscarCndResultadoPorId(@PathVariable Long id) {
        CndResultadoDTO resultado = cndResultadoService.buscarCndResultadoPorId(id);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um resultado de CND existente (usado pela PEC-4537 para edição e PEC-4963 para extração de dados)")
    public ResponseEntity<CndResultadoDTO> atualizarCndResultado(@PathVariable Long id, @Valid @RequestBody CndResultadoDTO dto) {
        CndResultadoDTO resultadoAtualizado = cndResultadoService.atualizarCndResultado(id, dto);
        return ResponseEntity.ok(resultadoAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um resultado de CND por ID (usado pela PEC-4538)")
    public ResponseEntity<Void> deletarCndResultado(@PathVariable Long id) {
        cndResultadoService.deletarCndResultado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Realiza o download do arquivo PDF de uma CND (PEC-4981)")
    public ResponseEntity<byte[]> downloadCndPdf(@PathVariable Long id) {
        byte[] pdfBytes = cndResultadoService.downloadPdf(id);
        String filename = cndResultadoService.gerarNomeArquivoPadronizado(cndResultadoService.getCndResultadoEntityById(id));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename); // Força o download
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
