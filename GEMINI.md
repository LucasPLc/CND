Excelente. Com base em todos os tickets e padrões fornecidos, gerei uma documentação Gemini.md extremamente completa e estruturada.

Este documento foi projetado para ser o "cérebro" do projeto, fornecendo todas as informações de negócio, requisitos técnicos, regras, padrões de código e arquitetura de forma coesa. A IA poderá usar este arquivo como uma fonte única de verdade para gerar o código completo e funcional do projeto CND de uma só vez.

Gemini.md - Especificação Completa do Projeto CND
1. Visão Geral e Objetivo do Projeto

O Projeto CND visa criar uma plataforma completa e automatizada para o gerenciamento de Certidões Negativas de Débitos (CNDs). O sistema deve permitir o cadastro de clientes, a configuração de consultas automáticas de CNDs nos âmbitos Federal, Estadual e Municipal, a extração de dados dos documentos emitidos, o armazenamento seguro dos PDFs e a visualização centralizada de todo o histórico fiscal.

O objetivo principal é eliminar a necessidade de consultas manuais, reduzir erros, garantir a rastreabilidade dos processos e fornecer uma ferramenta robusta para que os usuários do SAAM possam monitorar a regularidade fiscal de seus clientes de forma eficiente e proativa.

2. Padrões Gerais de Desenvolvimento SAAM

Todo o código e a interface do projeto devem seguir rigorosamente os padrões estabelecidos pela SAAM.

2.1. Orientações Gerais de UI/UX

Compressão de Imagens: É mandatório comprimir todas as imagens para reduzir o tamanho dos arquivos. Utilizar ferramentas como ILoveIMG ou WebsitePlanet.

Performance e Memória:

Evitar a instanciação de objetos (new) dentro de loops. Se possível, usar variáveis globais ou reutilizar instâncias.

Nunca instanciar objetos (new) dentro de um método render ou de renderização de componentes.

Rotinas de atualização em tempo real devem ter um delay configurável (ex: a cada 1 minuto) ou processar em lotes (ex: 100 em 100 registros) para não sobrecarregar o sistema.

Caminhos de Diretório: Usar sempre a barra normal (/) e única.

Ordem Padrão dos Botões na Tela: A disposição dos botões deve seguir esta ordem exata:

Inserir/Novo

Alterar direto na tabela (se aplicável)

Alterar em lote

Copiar/Copiar para outro mês

Salvar (novo/alteração)

Excluir linhas selecionadas

Atualizar/Recarregar dados

Filtragem avançada

Processar/Gerar/Atualizar apuração

Gerar Relatório/Danfe/PDF (não relacionado ao download da CND)

Gerar Excel da tabela

Importar Excel

Abrir planilha padrão para importação

Transportar dados entre meses

Configurar parâmetros da rotina

Gerar CSV

Abrir NF-e no SPED

Gerar XML

Download do PDF da CND (Ação específica desta rotina)

Legendas das cores

Abrir links externos

Barra de navegação da tabela (sempre por último)

Padrão de Campos na Grid: Nomes de colunas devem ser capitulares. Ex: Código do Município em vez de código do município.

Tooltips: Adicionar tooltips informativos em botões e campos complexos para guiar o usuário.

2.2. Padrões de Código e Nomenclatura

Estilo de Código: Utilizar CamelCase para variáveis (numeroConta), métodos (calcularSalarioMensal), classes (UsuarioRegistrado) e UPPER_CASE para constantes (TAMANHO_MAXIMO).

Pacotes: package br.com.sisaudcon.projeto.cnd

Log de Ações no Banco de Dados: A coluna linha (ou uma coluna de log similar) deve registrar a origem da alteração no formato AÇÃO-VERSÃO. Ex:

INSERT-7.5.8

UPDATE-7.5.8

PLAN-7.5.8 (Importado via Planilha)

ROBO-FEDERAL-7.5.8 (Gerado via Robô Federal)

ROBO-ESTADUAL-MG-7.5.8 (Gerado via Robô Estadual de MG)

PARSE-PDF-7.5.8 (Dados extraídos do PDF via job)

2.3. Padrões de Relatórios

JasperReports: Se houver relatórios, os arquivos .jrxml devem seguir o padrão nome_relatorio.jrxml (ex: relatorio_cnds_emitidas.jrxml).

2.4. Gerenciamento de Dependências

Utilizar caminhos relativos para dependências internas do projeto, com a referência SAAM-SPED em maiúsculas.

3. Arquitetura da Solução

O projeto será desenvolvido seguindo uma arquitetura de microsserviços, com um backend robusto em Java/Spring Boot e um frontend reativo.

Backend (API RESTful - Spring Boot):

Linguagem: Java 17+

Framework: Spring Boot 3+

Arquitetura: em camadas (Controller, Service, Repository).

Padrões: DTOs para transferência de dados, Exception Handlers globais para tratamento de erros padronizado.

Segurança: Spring Security para proteger os endpoints.

Frontend:

Framework: Angular, React ou Vue (a ser definido), seguindo os padrões SAAM de UI/UX.

Módulo de Robôs de Consulta (Web Scraping/Connectors):

Um módulo dedicado para as automações de consulta. Cada órgão (Federal, Estadual, Municipal) terá seu próprio conector (implementação de uma interface CndConnector).

Deve ser resiliente, com políticas de retry e tratamento de erros específicos (ex: site fora do ar, CAPTCHA, dados não encontrados).

Módulo de Processamento Agendado (Scheduled Tasks):

Usará @Scheduled do Spring para tarefas em background, como a extração de dados de PDFs e a execução periódica dos robôs de consulta.

Comunicação Externa (MCP Server / SAAM-CR):

Utilizará RestTemplate ou WebClient para se comunicar com outras APIs da SAAM, como o serviço de validação de clientes e o de busca de dados de empresas.

4. Modelo de Dados (Schema do Banco de Dados)

As tabelas a seguir formam a base do sistema. Usar PostgreSQL.

Generated sql
-- Tabela para armazenar dados das empresas-mãe, sincronizadas via API do SAAM
CREATE TABLE cnd_empresa (
    id INT PRIMARY KEY,                     -- ID da empresa vindo do SAAM
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    nome_empresa VARCHAR(255) NOT NULL,
    id_empresa VARCHAR(6) NOT NULL,         -- Código interno SAAM
    status_empresa VARCHAR(50) NOT NULL
);

-- Tabela para cadastrar os clientes (CNPJs) que terão suas CNDs monitoradas
CREATE TABLE cnd_cliente (
    id SERIAL PRIMARY KEY,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    nome_cliente VARCHAR(255),              -- Adicionado para facilitar visualização
    periodicidade INT NOT NULL,             -- Frequência de consulta em dias
    status_cliente VARCHAR(50) NOT NULL,    -- Ex: 'ATIVO', 'INATIVO'
    nacional BOOLEAN DEFAULT FALSE,         -- Monitorar CND Federal?
    municipal BOOLEAN DEFAULT FALSE,        -- Monitorar CND Municipal?
    estadual BOOLEAN DEFAULT FALSE,         -- Monitorar CND Estadual?
    fk_empresa INT NOT NULL,
    FOREIGN KEY (fk_empresa) REFERENCES cnd_empresa(id)
);

-- Tabela para armazenar o resultado de cada consulta de CND realizada
CREATE TABLE cnd_resultado (
    id SERIAL PRIMARY KEY,
    data_processamento TIMESTAMP WITH TIME ZONE NOT NULL,
    tipo_cnd VARCHAR(20) NOT NULL,          -- 'FEDERAL', 'ESTADUAL', 'MUNICIPAL'
    orgao_emissor VARCHAR(100),             -- Ex: 'Receita Federal', 'SEFAZ-MG', 'Pref. São Paulo'
    arquivo TEXT,                           -- Conteúdo do PDF da CND em Base64
    situacao VARCHAR(100),                  -- Ex: 'Negativa', 'Positiva com efeitos de negativa'
    data_emissao DATE,
    data_validade DATE,
    codigo_controle VARCHAR(255),
    status_processamento VARCHAR(50) NOT NULL, -- 'CONCLUIDO_COM_SUCESSO', 'ERRO_CONSULTA', 'EMISSOR_INDISPONIVEL', 'PENDENTE_EXTRACAO'
    mensagem_erro TEXT,                     -- Detalhes em caso de falha
    fk_cliente INT NOT NULL,
    FOREIGN KEY (fk_cliente) REFERENCES cnd_cliente(id)
);

-- Tabela para log de downloads
CREATE TABLE cnd_log_download (
    id SERIAL PRIMARY KEY,
    id_resultado INT NOT NULL,
    usuario_download VARCHAR(100) NOT NULL,
    data_download TIMESTAMP WITH TIME ZONE NOT NULL,
    ip_origem VARCHAR(45),
    FOREIGN KEY (id_resultado) REFERENCES cnd_resultado(id)
);

5. Especificação da API e Endpoints
5.1. API de Gerenciamento de Clientes (PEC-4924)

Esta API gerencia o ciclo de vida dos clientes a serem monitorados.

POST /api/v1/clientes: Cadastra um novo cliente.

Corpo da Requisição: CndClienteDTO

Validações: cnpj válido e único, periodicidade > 0, fk_empresa obrigatório.

Regra de Negócio: Se fk_empresa não existir na tabela cnd_empresa local, a API deve buscar os dados da empresa na API interna do SAAM e cadastrá-la. Se não encontrar no SAAM, retorna erro.

Retorno: 201 Created com o cliente criado.

PUT /api/v1/clientes/{id}: Atualiza um cliente existente.

Corpo da Requisição: CndClienteDTO

Retorno: 200 OK com o cliente atualizado.

DELETE /api/v1/clientes/{id}: Exclui um cliente.

Regra de Negócio: Só permite a exclusão se não houver registros na cnd_resultado vinculados a este cliente.

Retorno: 200 OK ou 204 No Content.

GET /api/v1/clientes: Lista todos os clientes com paginação e filtros.

Retorno: 200 OK com Page<CndClienteDTO>.

GET /api/v1/clientes/{id}: Busca um cliente pelo ID.

Retorno: 200 OK com CndClienteDTO.

5.2. API de Segurança e Validação de Acesso (PEC-4923)

Um Filter ou Interceptor do Spring deve ser implementado para proteger TODOS os endpoints da API.

Fluxo de Validação:

Para cada requisição recebida, extrair o IDCLIENTE (pode vir de um token JWT, header, etc.).

Fazer uma chamada GET para a API do SAAM-CR: http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById/{IDCLIENTE}?attribute=situacao.

Analisar a resposta:

Se situacao for 1, a requisição prossegue normalmente.

Se situacao for qualquer outro valor, a requisição é bloqueada com status 403 Forbidden e a mensagem {"error": "Acesso negado. Cliente sem autorização ativa."}.

Tratamento de Erro na Integração: Se a chamada à API do SAAM-CR falhar (timeout, 5xx, etc.), a requisição deve ser bloqueada com status 503 Service Unavailable e a mensagem {"error": "Serviço de validação indisponível. Tente novamente mais tarde."}.

5.3. API de Consulta de Resultados e Download (PEC-4981)

GET /api/v1/resultados: Lista os resultados das consultas de CND com filtros avançados (CNPJ, datas, situação, etc.).

Retorno: 200 OK com Page<CndResultadoDTO>.

GET /api/v1/resultados/{id}/download: Realiza o download do PDF da CND.

Regra de Negócio: Verifica se o registro id existe e se o campo arquivo não é nulo.

Retorno (Sucesso): 200 OK com o Content-Type: application/pdf e o Content-Disposition com o nome do arquivo padronizado: CND_{CNPJ}_{TIPO_CND}_{DATA_EMISSAO}.pdf. (Ex: CND_12345678000199_RFB_2025-06-24.pdf).

Retorno (Erro): 404 Not Found se o arquivo não existir.

Log: Registra a tentativa de download na tabela cnd_log_download.

5.4. Mapeamento de Exceções Padrão
Exceção	Status HTTP	Mensagem de Retorno (JSON)
ClienteNotFoundException	404	{"error": "Cliente não encontrado para o ID informado."}
EmpresaNotFoundException	404	{"error": "Empresa não encontrada no SAAM para o ID informado."}
ClienteVinculadoResultadoException	400	{"error": "Não é possível excluir o cliente. Existem resultados vinculados."}
MethodArgumentNotValidException	400	{"errors": [{"field": "cnpj", "message": "CNPJ inválido"}]}
ConstraintViolationException	409	{"error": "Já existe um cliente cadastrado com este CNPJ."}
SAAMIntegrationException	502	{"error": "Erro na comunicação com o serviço externo SAAM."}
ClienteNaoAutorizadoException	403	{"error": "Acesso negado. Cliente sem autorização ativa."}
ServicoValidacaoIndisponivelException	503	{"error": "Serviço de validação indisponível. Tente novamente mais tarde."}
Exception (Genérica)	500	{"error": "Erro interno no servidor. Tente novamente mais tarde."}
6. Funcionalidades Detalhadas (Épicos)
6.1. Épico 1: Tela de Gerenciamento e Consulta (Frontend)

História (PEC-4536, PEC-4961): Como usuário, quero uma tela para cadastrar e visualizar todas as CNDs monitoradas, com filtros e paginação.

Critérios de Aceite:

A tela deve conter uma grid exibindo dados das tabelas cnd_cliente e o último resultado de cnd_resultado.

Colunas visíveis: CNPJ Cliente, Nome Cliente, Periodicidade, Status Cliente, Nacional, Estadual, Municipal, Data do Último Processamento, Situação da Última Certidão, Validade da Última Certidão.

Filtros avançados por: CNPJ, Nome, Situação, Status e intervalo de datas.

Botões para CRUD de clientes (cnd_cliente).

Ações na linha para Editar e Excluir um cliente, seguindo as regras da API.

História (PEC-4981): Como usuário, quero baixar o PDF de uma CND consultada.

Critérios de Aceite:

Na grid de resultados, deve haver um ícone/botão de download por linha.

O botão só fica habilitado se o arquivo na tabela cnd_resultado estiver preenchido.

Ao clicar, a API /api/v1/resultados/{id}/download é chamada, e o browser inicia o download do PDF com o nome padronizado.

Deve haver feedback visual em caso de erro (ex: tooltip "Arquivo não encontrado").

6.2. Épico 2: Automação da Consulta de CNDs (Robôs)

História (PEC-4869): Como sistema, quero consultar automaticamente a CND Federal.

Critérios de Aceite:

Implementar um CndConnector para o site da Receita Federal: https://solucoes.receita.fazenda.gov.br/servicos/certidaointernet/pj/emitir.

O conector recebe um CNPJ, realiza a consulta e, se bem-sucedido, baixa o PDF da certidão.

Cria um novo registro em cnd_resultado com tipo_cnd = 'FEDERAL', o PDF em Base64 no campo arquivo, status_processamento = 'PENDENTE_EXTRACAO' e data_processamento atual.

Se o site estiver indisponível ou houver erro, registrar em cnd_resultado com status_processamento = 'ERRO_CONSULTA' e a mensagem de erro.

História (Agrupamento das PECs Estaduais/Municipais): Como sistema, quero consultar CNDs de diversas entidades.

Critérios de Aceite:

Criar implementações de CndConnector para cada URL funcional.

OK (Implementação Direta):

MG: https://www2.fazenda.mg.gov.br/... (PEC-4629)

MA: https://sistemas1.sefaz.ma.gov.br/... (PEC-4628)

GO: https://www.go.gov.br/... (PEC-4627)

DF: https://ww1.receita.fazenda.df.gov.br/... (PEC-4626, PEC-4617)

BA: https://servicos.sefaz.ba.gov.br/... (PEC-4625)

Palmas (TO): http://certidao.palmas.to.gov.br/... (PEC-4624)

Uberlândia (MG): http://portalsiat.uberlandia.mg.gov.br/... (PEC-4621)

Imperatriz (MA): https://nfse-ma-imperatriz.portalfacil.com.br/ (PEC-4619)

São Sebastião do Passé (BA): https://saosebastiaodopasse.saatri.com.br/ (PEC-4616)

Camaçari (BA): https://sefazweb.camacari.ba.gov.br/... (PEC-4539 - requer login)

Requer Análise Adicional (Spike Técnico):

CAPTCHA: TO (PEC-4630), Benevides (PA) (PEC-4622). Investigar uso de serviços de quebra de CAPTCHA.

Requer Login: Patrocínio (MG) (PEC-4620). A API deve armazenar credenciais de forma segura.

Site Inválido: Marabá (PA) (PEC-4623), Senador Canedo (GO) (PEC-4618). Marcar como "não implementável" e registrar o erro.

O fluxo de registro em cnd_resultado é o mesmo da CND Federal, mudando tipo_cnd e orgao_emissor.

6.3. Épico 3: Pós-Processamento e Extração de Dados do PDF

História (PEC-4963): Como sistema, quero ler o PDF da CND Federal baixado e extrair seus dados.

Critérios de Aceite:

Criar um job agendado (@Scheduled) que executa a cada 15 minutos (configurável via application.properties: cnd.resultado.scheduled.cron=0 */15 * * * *).

O job busca registros na cnd_resultado com status_processamento = 'PENDENTE_EXTRACAO'.

Para cada registro, decodifica o arquivo (Base64) para um stream de PDF.

Utiliza a biblioteca org.apache.pdfbox:pdfbox:2.0.30 para extrair o texto do PDF.

Usando expressões regulares (Regex), extrai: Situação, Data de Emissão, Data de Validade, Código de Controle.

Atualiza o registro no banco de dados com os dados extraídos e muda o status_processamento para CONCLUIDO_COM_SUCESSO.

Se a extração falhar (PDF malformado, texto não encontrado), o status_processamento deve ser alterado para ERRO_EXTRACAO e o erro logado. O job deve continuar para o próximo registro.

O job não deve sobrescrever campos que já foram preenchidos.

Criar testes unitários com mocks para a lógica de extração de PDF.

7. Stack Tecnológica e Dependências

Linguagem: Java 17

Framework: Spring Boot 3.x (Web, Data JPA, Security)

Banco de Dados: PostgreSQL

Build: Maven ou Gradle

Dependências Principais:

spring-boot-starter-web

spring-boot-starter-data-jpa

spring-boot-starter-security

postgresql (driver JDBC)

org.apache.pdfbox:pdfbox:2.0.30 (para extração de texto de PDF)

org.projectlombok:lombok (opcional, para reduzir boilerplate)

spring-boot-starter-validation (para validação de DTOs)

org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0 (para documentação Swagger/OpenAPI)

Frontend: A ser definido (Angular/React/Vue).

8. Configuração do Projeto (application.properties)
Generated properties
# Configurações do Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/saam_cnd
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=validate # Em produção, usar 'validate' ou 'none'
spring.jpa.show-sql=true

# Configuração do Job de Extração de PDF
cnd.resultado.scheduled.cron=0 */15 * * * *

# Configuração da API Externa de Validação
saam.cr.validation.url=http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById

# Configuração da API Externa de Dados da Empresa
saam.empresa.data.url=http://<URL_API_EMPRESA_SAAM>
IGNORE_WHEN_COPYING_START
content_copy
download
Use code with caution.
Properties
IGNORE_WHEN_COPYING_END