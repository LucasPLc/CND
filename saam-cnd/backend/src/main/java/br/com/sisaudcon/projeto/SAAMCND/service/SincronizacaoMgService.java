package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
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
public class SincronizacaoMgService {

    private static final Logger logger = LoggerFactory.getLogger(SincronizacaoMgService.class);

    private final CndClienteService cndClienteService;
    private final CndResultadoService cndResultadoService;

    @Autowired
    public SincronizacaoMgService(CndClienteService cndClienteService, CndResultadoService cndResultadoService) {
        this.cndClienteService = cndClienteService;
        this.cndResultadoService = cndResultadoService;
    }

    @Transactional
    public CndResultado sincronizarCndMinasGerais(Long clienteId, String tipoConsulta) { // tipoConsulta pode ser "ESTADUAL_MG", etc.
        CndCliente cliente = cndClienteService.getClienteEntityById(clienteId);
        logger.info("Iniciando sincronização CND Minas Gerais (mock) para cliente ID: {}, CNPJ: {}", cliente.getId(), cliente.getCnpj());

        // Simulação da consulta ao site de Minas Gerais
        CndResultado resultado = new CndResultado();
        resultado.setCliente(cliente);
        resultado.setDataProcessamento(LocalDateTime.now());
        resultado.setOrgaoEmissor("SEFAZ-MG (Mock)"); // Campo adicionado ao CndResultado e CndForm
        resultado.setTipoCertidao("Estadual (Mock MG)"); // Campo adicionado

        // Simular resposta
        resultado.setSituacao("Regular Fiscalmente (Mock MG)");
        resultado.setDataEmissao(LocalDate.now().minusDays((long) (Math.random() * 5)));
        resultado.setDataValidade(resultado.getDataEmissao().plusDays(60));
        resultado.setCodigoControle("MOCKMG" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        String mockPdfContent = "PDF Mockado - CND Estadual Minas Gerais - CNPJ: " + cliente.getCnpj();
        resultado.setArquivo(mockPdfContent.getBytes()); // Agora espera byte[]
        resultado.setStatusProcessamento("CONSULTA_REALIZADA"); // Para extração posterior se necessário
        resultado.setLinha("GERADO-PEC-4629-MOCK_MG");

        logger.info("Sincronização CND Minas Gerais (Mock) concluída para CNPJ {}", cliente.getCnpj());
        return cndResultadoService.salvarResultado(resultado);
    }
}
