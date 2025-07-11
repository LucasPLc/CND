package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndResultadoRepository;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndResultadoDTO;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CndResultadoService {

    private static final Logger logger = LoggerFactory.getLogger(CndResultadoService.class);

    private final CndResultadoRepository cndResultadoRepository;
    private final CndClienteService cndClienteService; // Para buscar cliente ao criar/atualizar resultado

    @Autowired
    public CndResultadoService(CndResultadoRepository cndResultadoRepository, CndClienteService cndClienteService) {
        this.cndResultadoRepository = cndResultadoRepository;
        this.cndClienteService = cndClienteService;
    }

    @Transactional
    public CndResultadoDTO criarCndResultado(CndResultadoDTO dto) {
        if (dto.getFkCliente() == null) {
            throw new BadRequestException("ID do Cliente (fkCliente) é obrigatório para criar um resultado de CND.");
        }
        CndCliente cliente = cndClienteService.getClienteEntityById(dto.getFkCliente());

        CndResultado cndResultado = new CndResultado();
        // mapDtoToEntity(dto, cndResultado); // Mapeamento movido para baixo após setar o cliente
        cndResultado.setCliente(cliente); // Associar cliente primeiro

        // Mapear DTO para Entidade, incluindo decodificação Base64 para byte[]
        if (dto.getDataProcessamento() != null) cndResultado.setDataProcessamento(dto.getDataProcessamento());
        if (dto.getArquivo() != null && !dto.getArquivo().isEmpty()) {
            try {
                cndResultado.setArquivo(Base64.getDecoder().decode(dto.getArquivo()));
            } catch (IllegalArgumentException e) {
                logger.error("Erro ao decodificar Base64 do arquivo para nova CND do cliente {}: {}", cliente.getId(), e.getMessage());
                throw new BadRequestException("Arquivo PDF (Base64) inválido.");
            }
        }
        if (dto.getSituacao() != null) cndResultado.setSituacao(dto.getSituacao());
        if (dto.getDataEmissao() != null) cndResultado.setDataEmissao(dto.getDataEmissao());
        if (dto.getDataValidade() != null) cndResultado.setDataValidade(dto.getDataValidade());
        if (dto.getCodigoControle() != null) cndResultado.setCodigoControle(dto.getCodigoControle());
        if (dto.getStatusProcessamento() != null) cndResultado.setStatusProcessamento(dto.getStatusProcessamento());
        else cndResultado.setStatusProcessamento("PENDENTE_EXTRACAO"); // Default se não especificado

        if (dto.getMensagemErroProcessamento() != null) cndResultado.setMensagemErroProcessamento(dto.getMensagemErroProcessamento());

        if (dto.getLinha() != null && !dto.getLinha().isEmpty()){
            cndResultado.setLinha(dto.getLinha());
        } else {
            cndResultado.setLinha("INSERT-PEC-XXXX"); // Genérico, idealmente o PEC específico da criação
        }
        if (dto.getTipoCertidao() != null) cndResultado.setTipoCertidao(dto.getTipoCertidao());
        if (dto.getOrgaoEmissor() != null) cndResultado.setOrgaoEmissor(dto.getOrgaoEmissor());
        if (dto.getObservacoes() != null) cndResultado.setObservacoes(dto.getObservacoes());


        CndResultado salvo = cndResultadoRepository.save(cndResultado);
        logger.info("Resultado CND criado com ID {} para o cliente ID {}", salvo.getId(), cliente.getId());
        return new CndResultadoDTO(salvo); // DTO construtor agora precisa lidar com byte[] para String Base64
    }

    @Transactional
    public CndResultado salvarResultado(CndResultado cndResultado) {
        // Usado internamente por outros serviços como o de consulta automática ou extração de PDF
        return cndResultadoRepository.save(cndResultado);
    }


    @Transactional(readOnly = true)
    public List<CndResultadoDTO> listarCndResultados(Map<String, String> filters) {
        Specification<CndResultado> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters.containsKey("fkCliente")) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("id"), Long.parseLong(filters.get("fkCliente"))));
            }
            if (filters.containsKey("cnpjCliente")) {
                 predicates.add(criteriaBuilder.equal(root.get("cliente").get("cnpj"), filters.get("cnpjCliente")));
            }
            if (filters.containsKey("situacao")) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("situacao")), "%" + filters.get("situacao").toLowerCase() + "%"));
            }
            if (filters.containsKey("statusProcessamento")) {
                predicates.add(criteriaBuilder.equal(root.get("statusProcessamento"), filters.get("statusProcessamento")));
            }
            if (filters.containsKey("dataEmissaoApos")) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataEmissao"), LocalDate.parse(filters.get("dataEmissaoApos"))));
            }
            if (filters.containsKey("dataEmissaoAntes")) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataEmissao"), LocalDate.parse(filters.get("dataEmissaoAntes"))));
            }
            // Adicionar mais filtros conforme PEC-4961 (nome cliente - precisa de join, etc)
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return cndResultadoRepository.findAll(spec).stream()
                .map(CndResultadoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CndResultadoDTO buscarCndResultadoPorId(Long id) {
        CndResultado resultado = cndResultadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado CND não encontrado para o ID: " + id));
        return new CndResultadoDTO(resultado);
    }

    @Transactional(readOnly = true)
    public CndResultado getCndResultadoEntityById(Long id) {
        return cndResultadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado CND não encontrado para o ID: " + id));
    }


    @Transactional
    public CndResultadoDTO atualizarCndResultado(Long id, CndResultadoDTO dto) {
        CndResultado cndResultadoExistente = cndResultadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado CND não encontrado para o ID: " + id));

        // Mapear DTO para Entidade, incluindo decodificação Base64 para byte[]
        if (dto.getDataProcessamento() != null) cndResultadoExistente.setDataProcessamento(dto.getDataProcessamento());
        if (dto.getArquivo() != null && !dto.getArquivo().isEmpty()) {
             try {
                cndResultadoExistente.setArquivo(Base64.getDecoder().decode(dto.getArquivo()));
            } catch (IllegalArgumentException e) {
                logger.error("Erro ao decodificar Base64 do arquivo para CND ID {}: {}", id, e.getMessage());
                throw new BadRequestException("Arquivo PDF (Base64) inválido.");
            }
        } else if (dto.getArquivo() != null && dto.getArquivo().isEmpty()) { // Permitir limpar o arquivo
            cndResultadoExistente.setArquivo(null);
        }

        if (dto.getSituacao() != null) cndResultadoExistente.setSituacao(dto.getSituacao());
        if (dto.getDataEmissao() != null) cndResultadoExistente.setDataEmissao(dto.getDataEmissao());
        if (dto.getDataValidade() != null) cndResultadoExistente.setDataValidade(dto.getDataValidade());
        if (dto.getCodigoControle() != null) cndResultadoExistente.setCodigoControle(dto.getCodigoControle());
        if (dto.getStatusProcessamento() != null) cndResultadoExistente.setStatusProcessamento(dto.getStatusProcessamento());
        if (dto.getMensagemErroProcessamento() != null) cndResultadoExistente.setMensagemErroProcessamento(dto.getMensagemErroProcessamento());

        if (dto.getLinha() != null && !dto.getLinha().isEmpty()){
            cndResultadoExistente.setLinha(dto.getLinha());
        } else if (cndResultadoExistente.getLinha() == null || !cndResultadoExistente.getLinha().startsWith("UPDATE-PEC-")) {
             cndResultadoExistente.setLinha("UPDATE-PEC-4537"); // PEC de edição
        }

        if (dto.getTipoCertidao() != null) cndResultadoExistente.setTipoCertidao(dto.getTipoCertidao());
        if (dto.getOrgaoEmissor() != null) cndResultadoExistente.setOrgaoEmissor(dto.getOrgaoEmissor());
        if (dto.getObservacoes() != null) cndResultadoExistente.setObservacoes(dto.getObservacoes());


        // Se fkCliente for fornecido no DTO e for diferente, atualiza a associação com o cliente
        if (dto.getFkCliente() != null && !dto.getFkCliente().equals(cndResultadoExistente.getCliente().getId())) {
            CndCliente novoCliente = cndClienteService.getClienteEntityById(dto.getFkCliente());
            cndResultadoExistente.setCliente(novoCliente);
        }

        CndResultado atualizado = cndResultadoRepository.save(cndResultadoExistente);
        logger.info("Resultado CND com ID {} atualizado.", atualizado.getId());
        return new CndResultadoDTO(atualizado); // DTO construtor agora precisa lidar com byte[] para String Base64
    }

    @Transactional
    public void deletarCndResultado(Long id) {
        if (!cndResultadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resultado CND não encontrado para o ID: " + id);
        }
        cndResultadoRepository.deleteById(id);
        logger.info("Resultado CND com ID {} deletado.", id);
    }

    @Transactional
    public byte[] downloadPdf(Long id) {
        CndResultado resultado = getCndResultadoEntityById(id);
        if (resultado.getArquivo() == null || resultado.getArquivo().length == 0) {
            throw new ResourceNotFoundException("Arquivo PDF não disponível para o resultado CND ID: " + id);
        }
        // Log de download (simples, pode ser mais elaborado)
        logger.info("Download solicitado para CND ID: {}, Cliente CNPJ: {}, Arquivo: {}",
            id, resultado.getCliente().getCnpj(), gerarNomeArquivoPadronizado(resultado));

        return resultado.getArquivo(); // Retorna diretamente o byte[]
    }

    public String gerarNomeArquivoPadronizado(CndResultado resultado) {
        // Formato: CND_CNPJ_ORGAO_DATAEMISSAO.pdf
        // ORGAO (Federal(RFB), Municipal (MUN), Estadual(EST)) - Precisamos de uma forma de determinar o órgão.
        // Por enquanto, usaremos um placeholder ou um campo na CndResultado se existir.
        // Para simplificar, vamos usar "GERAL" como órgão se não houver informação.
        String orgao = "GERAL"; // Placeholder
        String cnpj = resultado.getCliente().getCnpj().replaceAll("[^0-9]", "");
        String dataEmissao = resultado.getDataEmissao() != null ? resultado.getDataEmissao().toString() : "SEM_DATA";

        // Ex: CND_12345678000199_RFB_2025-06-24.pdf
        return String.format("CND_%s_%s_%s.pdf", cnpj, orgao, dataEmissao);
    }


    // private void mapDtoToEntity(CndResultadoDTO dto, CndResultado entity) { // Removido pois o mapeamento agora é inline
    // // Não mapear ID aqui, pois ele já existe ou será gerado
    // if (dto.getDataProcessamento() != null) entity.setDataProcessamento(dto.getDataProcessamento());
    // // if (dto.getArquivo() != null) entity.setArquivo(dto.getArquivo()); // Arquivo Base64 - Tratado no local da chamada
    // if (dto.getSituacao() != null) entity.setSituacao(dto.getSituacao());
    // if (dto.getDataEmissao() != null) entity.setDataEmissao(dto.getDataEmissao());
    // if (dto.getDataValidade() != null) entity.setDataValidade(dto.getDataValidade());
    // if (dto.getCodigoControle() != null) entity.setCodigoControle(dto.getCodigoControle());
    // if (dto.getStatusProcessamento() != null) entity.setStatusProcessamento(dto.getStatusProcessamento());
    // if (dto.getMensagemErroProcessamento() != null) entity.setMensagemErroProcessamento(dto.getMensagemErroProcessamento());
    // if (dto.getLinha() != null) entity.setLinha(dto.getLinha());
    // // O cliente é tratado separadamente
    // }

    // Para PEC-4963
    @Transactional(readOnly = true)
    public List<CndResultado> findResultadosParaExtracao() {
        // "concluido" aqui se refere ao status da consulta inicial da CND, antes da extração.
        // Usaremos o campo 'statusProcessamento' que criei.
        // Se a consulta inicial já preenche alguns dados e marca como "CONCLUIDO_CONSULTA",
        // e a extração é uma etapa posterior, o status inicial poderia ser algo como "PENDENTE_EXTRACAO_DADOS".
        // Vamos assumir que PEC-4869 (consulta) salva com statusProcessamento "CONSULTA_CONCLUIDA" ou similar
        // e PEC-4963 busca por esse status e situacao IS NULL.
        // Por enquanto, vou usar um status genérico "PENDENTE_EXTRACAO" como exemplo.
        // A lógica exata do fluxo de status precisa ser refinada.
        // A PEC-4963 diz "status = 'concluido' e situacao IS NULL"
        // Vamos assumir que "concluido" se refere a um statusProcessamento específico.
        return cndResultadoRepository.findByStatusProcessamentoAndSituacaoIsNull("CONSULTA_REALIZADA");
    }
}
