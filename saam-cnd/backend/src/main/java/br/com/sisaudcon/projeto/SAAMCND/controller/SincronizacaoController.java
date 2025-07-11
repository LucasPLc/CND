package br.com.sisaudcon.projeto.SAAMCND.controller;

import br.com.sisaudcon.projeto.SAAMCND.dto.CndResultadoDTO;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.service.SincronizacaoMgService;
// Importar outros serviços de sincronização aqui (Tocantins, etc.)

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sincronizacao")
@Tag(name = "Sincronização CND", description = "API para disparar sincronizações automáticas (mockadas) de CNDs específicas")
public class SincronizacaoController {

    private final SincronizacaoMgService sincronizacaoMgService;
    // Injetar outros serviços de sincronização

    @Autowired
    public SincronizacaoController(SincronizacaoMgService sincronizacaoMgService) {
        this.sincronizacaoMgService = sincronizacaoMgService;
    }

    @PostMapping("/minas-gerais/{clienteId}")
    @Operation(summary = "Dispara a sincronização (mock) da CND de Minas Gerais para um cliente (PEC-4629)")
    public ResponseEntity<CndResultadoDTO> sincronizarMinasGerais(@PathVariable Long clienteId, @RequestParam(defaultValue = "ESTADUAL_MG") String tipoConsulta) {
        CndResultado resultado = sincronizacaoMgService.sincronizarCndMinasGerais(clienteId, tipoConsulta);
        return ResponseEntity.ok(new CndResultadoDTO(resultado));
    }

    // Endpoints para outras sincronizações (PEC-4630, etc.) viriam aqui
    // Exemplo:
    // @PostMapping("/tocantins/{clienteId}")
    // @Operation(summary = "Dispara a sincronização (mock) da CND do Tocantins para um cliente (PEC-4630)")
    // public ResponseEntity<CndResultadoDTO> sincronizarTocantins(@PathVariable Long clienteId) {
    //     // Chamar o SincronizacaoTocantinsService
    //     // CndResultado resultado = sincronizacaoTocantinsService.sincronizar(clienteId);
    //     // return ResponseEntity.ok(new CndResultadoDTO(resultado));
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    // }
}
