package br.com.sisaudcon.projeto.SAAMCND.scheduler;

import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.service.CndResultadoService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PdfExtractionScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(PdfExtractionScheduledTask.class);
    // Formatador para datas como dd/MM/yyyy ou yyyy-MM-dd
    private static final DateTimeFormatter DATE_FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private final CndResultadoService cndResultadoService;

    @Autowired
    public PdfExtractionScheduledTask(CndResultadoService cndResultadoService) {
        this.cndResultadoService = cndResultadoService;
    }

    // Cron configurado em application.properties: cnd.resultado.scheduled.cron=0 */15 * * * *
    @Scheduled(cron = "${cnd.resultado.scheduled.cron}")
    @Transactional // Importante para operações de atualização no banco dentro do scheduled task
    public void processarResultadosParaExtracaoDePdf() {
        logger.info("Iniciando tarefa agendada de extração de dados de PDF...");
        // PEC-4963: status = 'concluido' e situacao IS NULL.
        // No CndResultadoService, findResultadosParaExtracao busca por statusProcessamento = "CONSULTA_REALIZADA" e situacao IS NULL.
        List<CndResultado> resultadosParaProcessar = cndResultadoService.findResultadosParaExtracao();

        if (resultadosParaProcessar.isEmpty()) {
            logger.info("Nenhum resultado de CND pendente de extração de PDF encontrado.");
            return;
        }

        logger.info("{} resultados de CND encontrados para extração de dados do PDF.", resultadosParaProcessar.size());

        for (CndResultado resultado : resultadosParaProcessar) {
            logger.info("Processando extração para CND Resultado ID: {}", resultado.getId());
            if (resultado.getArquivo() == null || resultado.getArquivo().length == 0) {
                logger.warn("Arquivo PDF não encontrado para CND Resultado ID: {}. Pulando.", resultado.getId());
                resultado.setStatusProcessamento("ERRO_EXTRACAO_PDF_AUSENTE");
                resultado.setMensagemErroProcessamento("Arquivo PDF (conteúdo binário) está vazio ou nulo.");
                resultado.setLinha("UPDATE-PEC-4963-ERRO_PDF_AUSENTE");
                cndResultadoService.salvarResultado(resultado);
                continue;
            }

            try {
                byte[] pdfBytes = resultado.getArquivo(); // Arquivo já é byte[]
                String textoDoPdf;
                try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                    if (document.isEncrypted()) {
                        logger.warn("PDF da CND Resultado ID: {} está criptografado. Não é possível extrair texto.", resultado.getId());
                        resultado.setStatusProcessamento("ERRO_EXTRACAO_PDF_CRIPTOGRAFADO");
                        resultado.setMensagemErroProcessamento("PDF está criptografado.");
                        resultado.setLinha("UPDATE-PEC-4963-ERRO_PDF_CRIPTOGRAFADO");
                        cndResultadoService.salvarResultado(resultado);
                        continue;
                    }
                    PDFTextStripper stripper = new PDFTextStripper();
                    textoDoPdf = stripper.getText(document);
                }

                // Simulação da extração de dados do textoDoPdf
                // Em um cenário real, seriam usadas Regex mais robustas ou análise posicional.
                boolean dadosExtraidos = extrairEPreencherDados(resultado, textoDoPdf);

                if (dadosExtraidos) {
                    resultado.setStatusProcessamento("EXTRACAO_CONCLUIDA");
                    resultado.setLinha("UPDATE-PEC-4963-SUCESSO");
                    logger.info("Extração de dados do PDF para CND Resultado ID: {} concluída com sucesso.", resultado.getId());
                } else {
                    // Se extrairEPreencherDados retornar false, significa que não encontrou os padrões esperados.
                    resultado.setStatusProcessamento("ERRO_EXTRACAO_DADOS_NAO_ENCONTRADOS");
                    resultado.setMensagemErroProcessamento("Não foi possível localizar todos os dados esperados no texto do PDF.");
                    resultado.setLinha("UPDATE-PEC-4963-ERRO_DADOS_NAO_ENCONTRADOS");
                    logger.warn("Não foi possível extrair todos os dados do PDF para CND Resultado ID: {}.", resultado.getId());
                }
                cndResultadoService.salvarResultado(resultado);

            } catch (IOException e) {
                logger.error("Erro de IO ao ler PDF da CND Resultado ID: {}: {}", resultado.getId(), e.getMessage());
                resultado.setStatusProcessamento("ERRO_EXTRACAO_IO");
                resultado.setMensagemErroProcessamento("Erro ao ler o conteúdo do PDF: " + e.getMessage());
                resultado.setLinha("UPDATE-PEC-4963-ERRO_IO");
                cndResultadoService.salvarResultado(resultado);
            } catch (IllegalArgumentException e) {
                logger.error("Erro ao decodificar Base64 do PDF para CND Resultado ID: {}: {}", resultado.getId(), e.getMessage());
                resultado.setStatusProcessamento("ERRO_EXTRACAO_BASE64");
                resultado.setMensagemErroProcessamento("Arquivo PDF (Base64) inválido: " + e.getMessage());
                resultado.setLinha("UPDATE-PEC-4963-ERRO_BASE64");
                cndResultadoService.salvarResultado(resultado);
            } catch (Exception e) { // Pega qualquer outra exceção inesperada
                 logger.error("Erro inesperado ao processar PDF da CND Resultado ID: {}: {}", resultado.getId(), e.getMessage(), e);
                resultado.setStatusProcessamento("ERRO_EXTRACAO_INESPERADO");
                resultado.setMensagemErroProcessamento("Erro inesperado durante a extração: " + e.getMessage());
                resultado.setLinha("UPDATE-PEC-4963-ERRO_INESPERADO");
                cndResultadoService.salvarResultado(resultado);
            }
        }
        logger.info("Tarefa agendada de extração de dados de PDF finalizada.");
    }

    private boolean extrairEPreencherDados(CndResultado resultado, String textoPdf) {
        // Mock de extração - Em um cenário real, estas regex seriam muito mais complexas e testadas.
        // O texto do PDF simulado pelo CndFederalService é:
        // "Este é um PDF mockado para CND Federal Negativa do CNPJ: ..."
        // "Situação da Certidão: Negativa de Débitos"
        // "Data de Emissão: DD/MM/YYYY"
        // "Data de Validade: DD/MM/YYYY"
        // "Código de Controle: XXXXXX"

        boolean situacaoOk = false;
        boolean dataEmissaoOk = false;
        boolean dataValidadeOk = false;
        boolean codigoControleOk = false;

        // Situação
        if (textoPdf.contains("Negativa de Débitos") || textoPdf.toLowerCase().contains("negativa de débitos")) {
            resultado.setSituacao("Negativa de Débitos");
            situacaoOk = true;
        } else if (textoPdf.contains("Positiva com Efeitos de Negativa") || textoPdf.toLowerCase().contains("positiva com efeitos de negativa")) {
            resultado.setSituacao("Positiva com Efeitos de Negativa");
            situacaoOk = true;
        } else if (textoPdf.contains("Positiva") || textoPdf.toLowerCase().contains("positiva")) { // Genérico
            resultado.setSituacao("Positiva");
            situacaoOk = true;
        }
        // Se o CndFederalService já preencheu a situação, podemos usar isso.
        // Mas a PEC-4963 sugere que a extração preenche. Se o CndFederalService já preencheu, este passo pode ser redundante ou apenas confirmatório.
        // Para o mock, vamos permitir que a extração sobrescreva se encontrar algo.


        // Data de Emissão (Ex: "Data de Emissão: 25/12/2023" ou "Emitida em 2023-12-25")
        Pattern pDataEmissao = Pattern.compile("(?:Data de Emissão|Emitida em)[:\\s]*(\\d{2}/\\d{2}/\\d{4}|\\d{4}-\\d{2}-\\d{2})", Pattern.CASE_INSENSITIVE);
        Matcher mDataEmissao = pDataEmissao.matcher(textoPdf);
        if (mDataEmissao.find()) {
            try {
                resultado.setDataEmissao(parseDate(mDataEmissao.group(1)));
                dataEmissaoOk = true;
            } catch (DateTimeParseException e) {
                logger.warn("Formato de data de emissão inválido no PDF (ID {}): {}", resultado.getId(), mDataEmissao.group(1));
            }
        }

        // Data de Validade (Ex: "Válida até: 25/06/2024" ou "Validade: 2024-06-25")
        Pattern pDataValidade = Pattern.compile("(?:Válida até|Validade)[:\\s]*(\\d{2}/\\d{2}/\\d{4}|\\d{4}-\\d{2}-\\d{2})", Pattern.CASE_INSENSITIVE);
        Matcher mDataValidade = pDataValidade.matcher(textoPdf);
        if (mDataValidade.find()) {
             try {
                resultado.setDataValidade(parseDate(mDataValidade.group(1)));
                dataValidadeOk = true;
            } catch (DateTimeParseException e) {
                logger.warn("Formato de data de validade inválido no PDF (ID {}): {}", resultado.getId(), mDataValidade.group(1));
            }
        }

        // Código de Controle (Ex: "Código de Controle: ABC123XYZ")
        Pattern pCodigoControle = Pattern.compile("(?:Código de Controle|Controle)[:\\s]*([A-Za-z0-9.-]+)", Pattern.CASE_INSENSITIVE);
        Matcher mCodigoControle = pCodigoControle.matcher(textoPdf);
        if (mCodigoControle.find()) {
            resultado.setCodigoControle(mCodigoControle.group(1).trim());
            codigoControleOk = true;
        }

        // Se o CndFederalService já preencheu com dados mockados, e o PDF simulado pelo CndFederalService
        // não tiver esses padrões exatos, a extração pode falhar.
        // Para o propósito deste mock, vamos assumir que o PDF gerado pelo CndFederalService
        // contém texto que pode ser minimamente parseado por estas regex simples.
        // Se o CndFederalService já tiver preenchido esses campos, esta etapa de extração pode ser opcional
        // ou apenas para confirmar/complementar.
        // Pela PEC-4963, a extração é quem preenche esses campos.

        // Para o mock, vamos considerar sucesso se pelo menos a situação foi identificada.
        // Em um caso real, todos os campos seriam importantes.
        return situacaoOk && dataEmissaoOk && dataValidadeOk && codigoControleOk;
    }

    private LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null) return null;
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER_ISO);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(dateString, DATE_FORMATTER_BR);
        }
    }
}
