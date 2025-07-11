package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndEmpresaRepository;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.ServicoExternoException; // Para simular falha no SAAM
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class CndEmpresaService {

    private static final Logger logger = LoggerFactory.getLogger(CndEmpresaService.class);

    private final CndEmpresaRepository cndEmpresaRepository;
    // private final RestTemplate restTemplate; // Para chamadas HTTP reais ao SAAM

    @Autowired
    public CndEmpresaService(CndEmpresaRepository cndEmpresaRepository /*, RestTemplateBuilder restTemplateBuilder*/) {
        this.cndEmpresaRepository = cndEmpresaRepository;
        // this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Obtém uma empresa pelo ID. Se não existir localmente, tenta buscar no "SAAM" (mockado).
     *
     * @param id O ID da empresa (considerado o ID do SAAM).
     * @return A entidade CndEmpresa.
     * @throws ResourceNotFoundException Se a empresa não for encontrada localmente nem no SAAM.
     * @throws ServicoExternoException Se houver falha na comunicação com o SAAM.
     */
    @Transactional
    public CndEmpresa getEmpresaByIdAndFetchIfNecessary(Long id) {
        Optional<CndEmpresa> empresaOptional = cndEmpresaRepository.findById(id);
        if (empresaOptional.isPresent()) {
            return empresaOptional.get();
        } else {
            logger.info("Empresa com ID {} não encontrada localmente. Tentando buscar no SAAM (mock)...", id);
            // Simulação da chamada ao SAAM para buscar dados da empresa
            // Em um cenário real, aqui ocorreria uma chamada HTTP para o SAAM.
            CndEmpresa empresaDoSaam = fetchEmpresaFromSaamMock(id); // Método mock

            if (empresaDoSaam != null) {
                empresaDoSaam.setLinha("INSERT-PEC-4924-SAAM_FETCH"); // Auditoria
                return cndEmpresaRepository.save(empresaDoSaam);
            } else {
                throw new ResourceNotFoundException("Empresa não encontrada no SAAM para o ID informado: " + id);
            }
        }
    }

    /**
     * Busca uma empresa pelo ID no repositório local.
     * @param id O ID da empresa.
     * @return A CndEmpresa encontrada.
     * @throws ResourceNotFoundException se a empresa não for encontrada.
     */
    public CndEmpresa findById(Long id) {
        return cndEmpresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com ID: " + id));
    }

    /**
     * Mock para simular a busca de dados de uma empresa em um sistema SAAM externo.
     * Em um projeto real, isso seria uma chamada HTTP.
     */
    private CndEmpresa fetchEmpresaFromSaamMock(Long idEmpresaSaam) {
        // Simulação: Se o ID for conhecido, retorna uma empresa mockada.
        // Isso deve ser substituído por uma chamada HTTP real ao SAAM.
        // Exemplo:ResponseEntity<CndEmpresaDTO> response = restTemplate.getForEntity("URL_SAAM/empresas/" + idEmpresaSaam, CndEmpresaDTO.class);
        // if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) { ... }

        if (idEmpresaSaam.equals(1L) || idEmpresaSaam.equals(2L)) { // Ajustado para incluir ID 2L usado no teste
            CndEmpresa empresa = new CndEmpresa();
            empresa.setId(idEmpresaSaam); // O ID que veio do SAAM
            empresa.setCnpj(idEmpresaSaam.equals(1L) ? "00.000.000/0001-00" : "11.111.111/0001-11");
            empresa.setNomeEmpresa("Empresa Mockada SAAM (ID: " + idEmpresaSaam + ")");
            empresa.setStatusEmpresa("ATIVO_SAAM");
            // Linha será definida ao salvar
            return empresa;
        } else if (idEmpresaSaam.equals(99L)) { // Simular erro de comunicação com SAAM
             throw new ServicoExternoException("Erro simulado na comunicação com o SAAM para buscar empresa ID: " + idEmpresaSaam);
        }
        // Se não for um ID mockado conhecido, simula que não encontrou no SAAM.
        return null;
    }

    @Transactional
    public CndEmpresa save(CndEmpresa cndEmpresa) {
        // A lógica da coluna "linha" deve ser mais específica dependendo da operação (criar/atualizar)
        // Isso será melhor tratado no CndClienteService ao criar/atualizar clientes.
        // Se for um save direto de empresa, precisamos de um contexto.
        if (cndEmpresa.getLinha() == null || cndEmpresa.getLinha().isEmpty()){
             cndEmpresa.setLinha("SAVE-GENERIC-PEC-XXXX");
        }
        return cndEmpresaRepository.save(cndEmpresa);
    }
}
