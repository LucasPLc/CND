package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndResultadoRepository; // Pode não ser necessário se CndResultadoService for usado
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class CndFederalService {

    private static final Logger logger = LoggerFactory.getLogger(CndFederalService.class);

    private final CndClienteService cndClienteService;
    private final CndResultadoService cndResultadoService; // Usar para salvar/atualizar

    @Autowired
    public CndFederalService(CndClienteService cndClienteService, CndResultadoService cndResultadoService) {
        this.cndClienteService = cndClienteService;
        this.cndResultadoService = cndResultadoService;
    }

    /**
     * Simula a consulta automática de uma CND Federal para um dado CNPJ de cliente.
     * Cria ou atualiza um CndResultado com dados mockados.
     *
     * @param clienteId O ID do CndCliente para o qual a consulta será realizada.
     * @return O CndResultado salvo (novo ou atualizado).
     */
    @Transactional
    public CndResultado consultarCndFederalParaCliente(Long clienteId) {
        CndCliente cliente = cndClienteService.getClienteEntityById(clienteId);
        logger.info("Iniciando consulta CND Federal (mock) para cliente ID: {}, CNPJ: {}", cliente.getId(), cliente.getCnpj());

        // Simulação da consulta ao site da Receita Federal
        // Em um cenário real, aqui haveria lógica com Selenium/Puppeteer, Jsoup, ou chamada de API se disponível.

        CndResultado resultado = new CndResultado();
        resultado.setCliente(cliente);
        resultado.setDataProcessamento(LocalDateTime.now());

        // Simular diferentes cenários de resposta
        double random = Math.random();
        if (random < 0.6) { // 60% chance de sucesso com CND Negativa
            resultado.setSituacao("Negativa de Débitos");
            resultado.setDataEmissao(LocalDate.now().minusDays((long) (Math.random() * 30))); // Emissão nos últimos 30 dias
            resultado.setDataValidade(resultado.getDataEmissao().plusDays(180)); // Validade de 180 dias
            resultado.setCodigoControle("MOCKRFB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            String mockPdfContentNegativa = "Este é um PDF mockado para CND Federal Negativa do CNPJ: " + cliente.getCnpj();
            resultado.setArquivo(mockPdfContentNegativa.getBytes()); // Agora espera byte[]
            resultado.setStatusProcessamento("CONSULTA_REALIZADA"); // Status para ser pego pela PEC-4963
            resultado.setLinha("GERADO-PEC-4869-MOCK-NEGATIVA");
            logger.info("CND Federal Mock: Negativa para CNPJ {}", cliente.getCnpj());

        } else if (random < 0.8) { // 20% chance de Positiva com Efeitos de Negativa
            resultado.setSituacao("Positiva com Efeitos de Negativa");
            resultado.setDataEmissao(LocalDate.now().minusDays((long) (Math.random() * 15)));
            resultado.setDataValidade(resultado.getDataEmissao().plusDays(90));
            resultado.setCodigoControle("MOCKRFB-PEN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            String mockPdfContentPositivaEfeitosNeg = "Este é um PDF mockado para CND Federal Positiva com Efeitos de Negativa do CNPJ: " + cliente.getCnpj();
            resultado.setArquivo(mockPdfContentPositivaEfeitosNeg.getBytes()); // Agora espera byte[]
            resultado.setStatusProcessamento("CONSULTA_REALIZADA");
            resultado.setLinha("GERADO-PEC-4869-MOCK-POSITIVA_EFEITOS_NEG");
            logger.info("CND Federal Mock: Positiva com Efeitos de Negativa para CNPJ {}", cliente.getCnpj());

        } else { // 20% chance de erro ou indisponibilidade
            resultado.setSituacao("Erro na Consulta");
            resultado.setDataEmissao(null);
            resultado.setDataValidade(null);
            resultado.setCodigoControle(null);
            resultado.setArquivo(null);
            resultado.setStatusProcessamento("ERRO_CONSULTA");
            resultado.setMensagemErroProcessamento("Simulação: Site da Receita Federal indisponível ou CNPJ com pendências não cobertas.");
            resultado.setLinha("ERRO-PEC-4869-MOCK-CONSULTA");
            logger.warn("CND Federal Mock: Erro na consulta para CNPJ {}", cliente.getCnpj());
        }

        // Salva o resultado usando o CndResultadoService
        // Não há um DTO aqui, pois estamos criando a entidade diretamente.
        // O CndResultadoService.salvarResultado pode ser um método que recebe a entidade.
        return cndResultadoService.salvarResultado(resultado);
    }
}
