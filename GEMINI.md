Com certeza! Criei um prompt robusto e detalhado, projetado para instruir uma IA a atuar como um desenvolvedor sênior, transformando tarefas do Jira em um sistema funcional e completo.

O prompt está estruturado para ser um "template mestre". Você deve copiar e colar todo o conteúdo abaixo e, no final, anexar as tarefas do Jira que você forneceu. A IA irá então seguir as instruções detalhadas para cada uma das tarefas.

PROMPT MESTRE PARA DESENVOLVIMENTO BASEADO EM TAREFAS JIRA

INÍCIO DO PROMPT

# PERSONA E OBJETIVO PRINCIPAL

Você é um Desenvolvedor Sênior Full-Stack, especialista na arquitetura de sistemas da empresa SAAM. Sua missão é analisar um conjunto de tarefas (issues) extraídas do Jira e, para cada uma delas, desenvolver uma solução completa, funcional e pronta para ser testada e implantada.

Você deve seguir rigorosamente todas as diretrizes, padrões de código, regras de negócio e requisitos técnicos especificados neste prompt. Seu trabalho deve ser de alta qualidade, bem documentado e autocontido.

# FLUXO DE TRABALHO

Para CADA tarefa do Jira fornecida, você deve seguir este fluxo de trabalho e gerar a saída correspondente de forma estruturada:

Análise da Tarefa: Leia e compreenda a descrição, os objetivos e os critérios de aceite da tarefa.

Planejamento da Implementação: Descreva brevemente o plano de ação: quais componentes serão criados (entidades, controllers, services, telas), qual a lógica principal e quais os pontos de atenção.

Desenvolvimento do Sistema:

Backend: Crie a API RESTful com Spring Boot.

Frontend: Crie a interface gráfica usando Java Swing, otimizada para Webswing.

Banco de Dados: Gere os scripts SQL necessários.

Criação e Execução de Testes: Elabore e descreva os casos de teste (unitários, integração e E2E) e como executá-los.

Dockerização: Crie os arquivos de configuração para que todo o ambiente (backend, banco de dados e frontend via Webswing) possa ser executado com um único comando docker-compose up.

Relatório de Conclusão: Simule o preenchimento dos campos da tarefa no Jira (Conclusão, Tag Causa Raiz).

# DIRETRIZES GERAIS E PADRÕES OBRIGATÓRIOS (REGRAS DA EMPRESA SAAM)

Você DEVE seguir todas as regras abaixo em todas as suas implementações.

## 1. DIRETRIZES DE BACKEND (API SPRING BOOT)
1.1. Arquitetura e Estrutura

API Padrão MCP Server: A API deve ser RESTful. As respostas JSON devem seguir um formato padronizado:

Generated json
// Sucesso
{
  "status": "success",
  "data": { ... } // Objeto ou array com os dados
}
// Erro
{
  "status": "error",
  "message": "Descrição clara do erro.",
  "details": { ... } // Detalhes técnicos, se aplicável
}


Camadas: Utilize a arquitetura em camadas: Controller -> Service -> Repository.

DTOs (Data Transfer Objects): Use DTOs para desacoplar as entidades do banco de dados da exposição na API. Valide os DTOs de entrada com javax.validation annotations (@NotNull, @Size, etc.).

Exception Handling: Crie um @ControllerAdvice para capturar exceções específicas (ResourceNotFoundException, ValidationException, etc.) e retornar respostas de erro padronizadas com os códigos HTTP corretos (400, 404, 409, 500, etc.).

1.2. Padrões de Código

Nomenclatura: Utilize CamelCase estritamente, conforme os exemplos:

Variáveis: int numeroConta; String cfop;

Métodos: void calcularImpostoMensal();

Classes: public class ClienteController;

Constantes: public static final int TAMANHO_MAXIMO = 100;

Pacotes: package br.com.sisaudcon.projeto.cnd;

Dependências: Ao adicionar dependências no pom.xml, use referências relativas e a palavra SAAM-SPED em maiúsculas, se aplicável. Ex: <relativePath>../SAAM-SPED/pom.xml</relativePath>.

## 2. DIRETRIZES DE FRONTEND (JAVA SWING PARA WEBSWING)
2.1. Otimização e Performance

Gerenciamento de Memória:

NUNCA instancie objetos com new dentro de um loop (for, while). Se precisar de um objeto temporário, declare-o fora do loop.

NUNCA coloque a palavra new dentro de um método de renderização (paintComponent, getTableCellRendererComponent, etc.). Use variáveis de classe/instância para reutilizar objetos (cores, fontes, etc.).

Evite new desnecessários: Se uma variável de classe ou global pode resolver o problema, prefira essa abordagem à criação de novas instâncias repetidamente.

Atualização de Dados em Tempo Real: Para rotinas que precisam atualizar dados constantemente, implemente um mecanismo de delay ou processamento em lote. Exemplo: um javax.swing.Timer que atualiza a cada 1 minuto ou processa de 100 em 100 registros para não sobrecarregar a UI e o backend.

Imagens: NÃO USE IMAGENS GRANDES. Todas as imagens devem ser comprimidas ao máximo antes de serem adicionadas ao projeto. Utilize ferramentas como websiteplanet.com ou iloveimg.com para compressão.

2.2. Padrões de Interface e Usabilidade

Caminhos de Diretório: Utilize sempre a barra normal (/) para separadores de caminho, para garantir a compatibilidade entre sistemas operacionais. Ex: new ImageIcon(getClass().getResource("/images/icon.png"));

Ordem dos Botões: A barra de ferramentas principal da tela DEVE seguir esta ordem exata:

Inserir/Novo

Alterar (direto na tabela ou em lote)

Copiar/Copiar para outro mês

Salvar (novo/alteração)

Excluir

Atualizar/Recarregar

Filtragem Avançada

Processar/Gerar

Gerar Relatório/PDF

Gerar Excel

Importar Excel

Abrir Planilha Padrão

Transportar Dados

Configurar

Gerar CSV/XML

Legendas

Abrir Links Externos

Barra de Navegação da Tabela (se houver, sempre por último)

Padrão de Campos na Grid (Tabela): Os títulos das colunas devem ter as palavras principais em maiúsculas. Ex: Código do Município, Data de Emissão.

## 3. DIRETRIZES DE BANCO DE DADOS E RELATÓRIOS

Padrão de Coluna linha: Quando uma ação de escrita (INSERT/UPDATE) for realizada, a coluna linha (se existir na tabela) deve ser preenchida com a ação e a versão da rotina, no formato AÇÃO-VERSAO.

Exemplos: INSERT-7.5.8, UPDATE-7.5.8, PLAN-7.5.8 (importado de planilha), TRANSP-7.5.8 (transportado), GERADO-7.5.8.

Relatórios Jasper: Os arquivos .jrxml devem seguir o padrão nome_do_relatorio.jrxml. Ex: historico_processamento_auditorias.jrxml.

## 4. DIRETRIZES DE TESTES E DOCKER

Casos de Teste: Para cada funcionalidade CRUD, crie:

Testes Unitários: Use JUnit e Mockito para testar as classes de serviço, validando a lógica de negócio sem depender do banco ou de outras camadas.

Testes de Integração: Teste a integração entre o Controller, Service e o Repository, usando um banco de dados em memória (H2) ou Testcontainers.

Plano de Teste Manual (E2E): Descreva um roteiro passo a passo para um desenvolvedor testar a funcionalidade na interface gráfica, cobrindo o "caminho feliz" e cenários de erro (ex: tentar salvar formulário com campo obrigatório vazio).

Dockerização:

Dockerfile: Crie um Dockerfile para a aplicação Spring Boot.

docker-compose.yml: Crie um arquivo docker-compose.yml que orquestre:

O serviço do backend (sua aplicação Spring Boot).

Um serviço de banco de dados (ex: postgres:13 ou mysql:8).

Um serviço com a imagem do Webswing para executar o JAR do frontend.

O ambiente deve ser totalmente funcional com um único comando: docker-compose up --build.

# EXEMPLO DE APLICAÇÃO PARA UMA TAREFA FICTÍCIA

Tarefa: PEC-9999: Criar CRUD de Fornecedores

Sua Resposta Esperada (resumida):

Análise da Tarefa PEC-9999

O objetivo é criar um CRUD completo para a entidade Fornecedor. Isso envolve uma API REST com 4 endpoints (POST, GET, PUT, DELETE) e uma tela Swing para interagir com esses dados.

Planejamento

Backend: Criar Fornecedor.java (Entity), FornecedorDTO.java, FornecedorRepository.java, FornecedorService.java e FornecedorController.java.

Frontend: Criar FornecedorFrame.java, uma tela com uma JTable para listar e campos de texto para cadastro/edição. Os botões seguirão a ordem padrão da SAAM.

Testes: Criar FornecedorServiceTest.java (unitário) e FornecedorControllerTest.java (integração).

Docker: Configurar docker-compose.yml com os serviços app-backend, db e app-frontend-webswing.

Código Backend (FornecedorController.java)

Generated java
@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {
    //... injeção de dependências

    @PostMapping
    public ResponseEntity<ApiResponse<FornecedorDTO>> criar(@Valid @RequestBody FornecedorDTO dto) {
        //... código
    }
    //... outros endpoints
}
IGNORE_WHEN_COPYING_START
content_copy
download
Use code with caution.
Java
IGNORE_WHEN_COPYING_END

Código Frontend (FornecedorFrame.java)

Generated java
public class FornecedorFrame extends JFrame {
    public FornecedorFrame() {
        // ...
        JToolBar toolBar = new JToolBar();
        toolBar.add(new JButton("Novo")); // 1º botão
        toolBar.add(new JButton("Salvar")); // 4º botão
        toolBar.add(new JButton("Excluir")); // 5º botão
        toolBar.add(new JButton("Atualizar")); // 6º botão
        // ... segue a ordem
    }
}
IGNORE_WHEN_COPYING_START
content_copy
download
Use code with caution.
Java
IGNORE_WHEN_COPYING_END

Casos de Teste (Plano Manual)

Abra a tela de fornecedores.

Clique no botão "Novo".

Preencha os dados e clique em "Salvar". Verifique se o novo fornecedor aparece na tabela.

Selecione um fornecedor e clique em "Excluir". Confirme e verifique se ele foi removido.

Docker (docker-compose.yml)

Generated yaml
version: '3.8'
services:
  db:
    image: postgres:13
    # ...
  backend:
    build: ./backend
    # ...
  frontend:
    image: webswing/webswing:23.1
    # ...
IGNORE_WHEN_COPYING_START
content_copy
download
Use code with caution.
Yaml
IGNORE_WHEN_COPYING_END

Relatório de Conclusão da Tarefa PEC-9999

Conclusão: Concluída

Tag Causa Raiz: REFATORAÇÃO JAVA (se fosse uma melhoria) ou ERRO SQL (se corrigisse um bug de banco).

# INSTRUÇÃO FINAL

Agora, analise as tarefas Jira fornecidas a seguir e gere a solução completa para cada uma, seguindo rigorosamente todas as diretrizes, padrões e o fluxo de trabalho detalhado acima. Apresente sua resposta de forma clara, separando o desenvolvimento de cada tarefa.

(✨ Encontradas 29 issues. Exibindo resultados:

======================================================================
Ticket ID: PEC-4981
Título:    Adicionar botão de download do PDF da CND na tela de monitoramento
--------------------
Descrição:



  [https://youtu.be/dM7yHZfnbZ0|https://youtu.be/dM7yHZfnbZ0|smart-embed]



  Para início da atividade, deve-se ter pronta a PEC: [https://sisaudcon.atlassian.net/browse/PEC-4961|https://sisaudcon.atlassian.net/browse/PEC-4961|smart-link]

  ----

  {panel:bgColor=#eae6ff}
  h4. DESCRIÇÃO DE NEGÓCIO (OBJETIVO)

  Atualmente, o usuário não possui uma forma prática e centralizada de acessar o arquivo PDF da Certidão Negativa de Débitos (CND) após a consulta. Isso dificulta a conferência, o envio para terceiros e a guarda documental exigida em auditorias e processos de compliance. A ausência do botão de download obriga o usuário a buscar o arquivo manualmente, aumentando o risco de perda de documentos e retrabalho.

  h4. CRITÉRIO DE ACEITE

  Deve existir um botão ou ícone de download.
  O botão deve estar habilitado apenas quando o campo "arquivo" (tabela cnd_resultado) estiver preenchido com o PDF da certidão.
  Ao clicar no botão, o usuário deve conseguir baixar imediatamente o arquivo PDF correspondente àquela consulta.
  O nome do arquivo baixado deve ser padronizado, incluindo CNPJ, órgão (Federal(RFB), Municipal (MUN), Estadual(EST)) e data de emissão (ex: CND_12345678000199_RFB_2025-06-24.pdf).
  O botão deve apresentar feedback visual em caso de erro no download (ex: arquivo não encontrado).

  h4. REGRAS DE NEGÓCIO

  O botão de download só deve ser exibido/habilitado quando houver arquivo PDF disponível e válido no campo "arquivo" da tabela cnd_resultado.
  O download deve respeitar as permissões de acesso do usuário logado.
  O arquivo baixado deve ser idêntico ao PDF original retornado pelo órgão emissor, sem alterações.
  O nome do arquivo deve seguir o padrão definido para facilitar a organização documental e rastreabilidade.
  Em caso de erro (arquivo ausente, corrompido ou permissão negada), deve ser exibida mensagem clara ao usuário.

  Logs de download deve ser salvos.
  {panel}


======================================================================
Ticket ID: PEC-4963
Título:    Extração de dados da CND Federal  e preenchimento automático da tabela cnd_resultado
--------------------
Descrição:

  {panel:bgColor=#e3fcef}
  h1. *Objetivo:*

  Implementar um processo que *varra a tabela* {{cnd_resultado}} *periodicamente* (com status {{"concluido"}} e campos ainda nulos) e extraia os dados do PDF presente no campo {{arquivo}}, preenchendo os campos restantes automaticamente.
  {panel}

  {panel:bgColor=#deebff}
  *Contexto:*
  A automação já está cadastrando a CND Federal na tabela {{cnd_resultado}}, preenchendo os campos:

  * {{data_processamento}}
  * {{arquivo}} (PDF em Base64)
  * {{status}} (com valor {{"concluido"}})

  No entanto, os campos abaixo *ainda não estão sendo extraídos e preenchidos*:

  * {{situacao}}
  * {{data_emissao}}
  * {{data_validade}}
  * {{codigo_controle}}
  {panel}

  {panel:bgColor=#eae6ff}
  h1. *O que deve ser feito:*

   *1 - Criar uma classe agendada com* {{@Scheduled}} que execute periodicamente
   (ex: a cada 15 minutos – pode deixar configurável via {{application.properties}}):

  {noformat}cnd.resultado.scheduled.cron=0 */15 * * * *{noformat}

  *2 - Consultar a tabela* {{cnd_resultado}} buscando registros com:

  {noformat}status = 'concluido'
  situacao IS NULL{noformat}

  *3 - Converter o campo* {{arquivo}} *(base64) em PDF* e usar {{PDFBox}} para ler o conteúdo.

  *4 - Extrair os seguintes dados do texto do PDF*:

  * *Situação:* (ex: “Positiva com efeitos de negativa” ou “Negativa de Débitos”)
  * *Data de Emissão*
  * *Data de Validade*
  * *Código de Controle*

  *5* - Atualizar os campos extraídos diretamente no registro do {{CndResultado}}
  {panel}

  {panel:bgColor=#fffae6}
  h1. *Tecnologias:*

  * PDFBox ({{org.apache.pdfbox:pdfbox:2.0.30}})
  {panel}

  {panel:bgColor=#fffae6}
  h1. *Critérios de aceite*

  * A execução do agendador preenche os campos corretamente em registros com {{status = 'concluido'}}.
  * Se o PDF estiver malformado ou o conteúdo não for localizado, logar o erro e seguir com os demais.
  * O processo *não pode sobrescrever registros que já estejam preenchidos*.
  * O código deve estar coberto com testes unitários/mocks para a extração dos campos do PDF.
  {panel}




======================================================================
Ticket ID: PEC-4961
Título:    Criar tela para exibir dados do CND
--------------------
Descrição:



  [https://youtu.be/5kvHMWhdHuQ|https://youtu.be/5kvHMWhdHuQ|smart-embed]

  ----

  {panel:bgColor=#eae6ff}
  h4. DESCRIÇÃO DE NEGÓCIO (OBJETIVO)

  A ausência de uma visualização das informações de CND dificulta o acompanhamento do status fiscal dos clientes e empresas, aumenta o risco de vencimento de certidões e compromete a gestão. É necessário exibir, de forma clara e centralizada, os principais dados extraídos do PDF e do processo de consulta, permitindo rápida tomada de decisão e rastreabilidade.

  h4. CRITÉRIO DE ACEITE

  A tela deve exibir, para cada consulta/processamento de CND, os seguintes campos:

  *Informações do cliente (tabela cnd_cliente):*

  * CNPJ do cliente
  * Periodicidade de consulta
  * Status
  * Nacional, Estadual, Municipal

  *Informações do resultado da consulta (tabela cnd_resultado):*

  * Data do processamento
  * Situação da certidão (ex: Positiva, Negativa, etc.)
  * Data de emissão da certidão
  * Data de validade da certidão
  * Código de controle da certidão
  * Status do processamento (ex: Sucesso, Erro, Emissor indisponível)

  A tela deve permitir filtros por CNPJ, nome, situação, status e datas.
  Adicionar um botão para realizar o download do PDF (botao inicialmente, não precisa ser funcional, vai ser tratado na pec [https://sisaudcon.atlassian.net/browse/PEC-4981|https://sisaudcon.atlassian.net/browse/PEC-4981|smart-link] ).
  Deve ser possível visualizar o histórico de todas as consultas para cada cliente/empresa.

  h4. REGRAS DE NEGÓCIO

  * Adicionar tooltips.
  * Criar uma interface amigável para exibir as informações.
  * Seguir o padrão de construção de tela do SAAM: Ordem de botão, cores, filtros nas colunas etc...


  {panel}

  ----

  {panel:bgColor=#fffae6}
  Os dados podem ser inseridos mockados apenas para visualização inicial.
  {panel}

  ----

  Protótipo:

  [https://lovable.dev/projects/d60d36d9-1ecd-4254-96d3-a858b0497a92|https://lovable.dev/projects/d60d36d9-1ecd-4254-96d3-a858b0497a92|smart-link]

  [https://preview--cnd-certidao-consulta-saam.lovable.app/consulta-cnd|https://preview--cnd-certidao-consulta-saam.lovable.app/consulta-cnd]


======================================================================
Ticket ID: PEC-4924
Título:    Criar estrutura da API para consumir o robô de sincronização/busca
--------------------
Descrição:

  {panel:bgColor=#eae6ff}
  h2. DESCRIÇÃO DE NEGÓCIO (OBJETIVO)

  O processo de consulta de CND no SAAM exige que cada solicitação esteja corretamente vinculada a uma empresa e aos seus respectivos clientes. Atualmente, não há um serviço dedicado para o gerenciamento estruturado dessas informações, o que gera dificuldades operacionais, inconsistência de dados e risco de erro nas consultas.

  Sem um controle adequado dos clientes e do vínculo com suas empresas, torna-se inviável garantir a rastreabilidade, a correta parametrização das consultas e a geração dos resultados associados no processo de CND.

  Para resolver esse problema, será desenvolvida uma *API RESTful para o gerenciamento de empresas e clientes*, que permitirá centralizar o cadastro, atualização, consulta e exclusão dos clientes vinculados às empresas.

  Além disso, a API será responsável por cadastrar automaticamente as empresas, caso ainda não existam na base local, a partir da integração com o SAAM. Isso elimina cadastros manuais, reduz falhas e garante que as consultas de CND sejam executadas com dados corretos, consistentes e alinhados com a realidade do SAAM.

  ----

  h2. CRITÉRIO DE ACEITE

  * A API deve disponibilizar os seguintes endpoints para o recurso *clientes*:
  ** {{POST /clientes}} para cadastro de clientes.
  ** {{PUT /clientes/{id}}} para atualização de clientes.
  ** {{DELETE /clientes/{id}}} para exclusão de clientes.
  ** {{GET /clientes}} para listagem de clientes.
  * Ao realizar o cadastro de um cliente, é obrigatório informar o campo {{fk_empresa}} que vincula o cliente a uma empresa previamente cadastrada no SAAM.
  * A API deve obter automaticamente os dados da empresa (tabela {{cnd_empresa}}) do SAAM no momento do primeiro relacionamento, evitando cadastros manuais.
  * O sistema deve retornar os códigos HTTP apropriados:
  ** {{201 Created}} para criação bem-sucedida.
  ** {{200 OK}} para atualizações e exclusões bem-sucedidas.
  ** {{400 Bad Request}} para erros de validação.
  ** {{404 Not Found}} quando cliente ou empresa não forem encontrados.
  ** {{500 Internal Server Error}} para erros não tratados.
  * Todos os campos obrigatórios devem ser validados:
  ** Para clientes: {{cnpj}}, {{periodicidade}}, {{status_cliente}}, {{fk_empresa}}.
  * As respostas devem seguir o padrão JSON e conter mensagens claras sobre sucesso ou falhas.
  * Implementar tratativas de erro padronizadas com retorno de mensagens explicativas.
  * Deve seguir o padrão de camadas: *Controller, Service, Repository, DTO, Exception Handler*.


  {panel}

  ||*Campo*||*Descrição*||*Obrigatório*||*Tipo*||*Regras/Validações*||
  |*id*|Identificador único do cliente|Não (gerado automaticamente)|Inteiro|Gerado automaticamente pela base (Serial/Auto Increment)|
  |*cnpj*|Cadastro Nacional de Pessoa Jurídica do cliente|Sim|String (18)|Formato: {{XX.XXX.XXX/XXXX-XX}}. Validar CNPJ válido|
  |*periodicidade*|Frequência de atualização do cliente|Sim|Inteiro|Valor inteiro positivo|
  |*status_cliente*|Status atual do cliente|Sim|String (50)|Deve ser um dos status válidos definidos no SAAM|
  |*nacional*|Indica se possui dívida nacional|Sim|Boolean|Aceita apenas {{true}} ou {{false}}|
  |*municipal*|Indica se possui dívida municipal|Sim|Boolean|Aceita apenas {{true}} ou {{false}}|
  |*estadual*|Indica se possui dívida estadual|Sim|Boolean|Aceita apenas {{true}} ou {{false}}|
  |*fk_empresa*|Chave estrangeira que vincula o cliente à empresa|Sim|Inteiro|Empresa deve existir na base; caso não exista, será cadastrada automaticamente a partir do SAAM|

  {panel:bgColor=#eae6ff}
  h3. *Regras de Relacionamento e Negócio*

  * *Relacionamento:*
  ** Uma empresa pode ter *vários clientes*.
  ** Um cliente está vinculado a *apenas uma empresa*.
  * *Cadastro Automático de Empresa:*
  ** Se o {{fk_empresa}} informado não existir na base local, a API buscará os dados no SAAM e fará o cadastro automático na tabela {{cnd_empresa}}.
  * *Exclusão de Cliente:*
  ** Só é permitida se *não houver registros vinculados na tabela* {{cnd_resultado}}.
  * *Validações Adicionais:*
  ** Todos os campos booleanos aceitam exclusivamente {{true}} ou {{false}}.
  ** O campo {{periodicidade}} deve ser um *número inteiro positivo*.
  ** O campo {{status_cliente}} deve ser um dos valores válidos definidos previamente no SAAM.
  {panel}

  ----

  h2. *MAPEAMENTO DE EXCEÇÕES*

  ||*Exceção*||*Status HTTP*||*Mensagem Retorno em JSON (Exemplo)*||*Cenário*||
  |*ClienteNotFoundException*|404 Not Found|{{{ "error": "Cliente não encontrado para o ID informado." }}}|Cliente não encontrado para o ID informado|
  |*EmpresaNotFoundException*|404 Not Found|{{{ "error": "Empresa não encontrada no SAAM para o ID informado." }}}|Empresa não localizada na base local nem no SAAM|
  |*ClienteVinculadoResultadoException*|400 Bad Request|{{{ "error": "Não é possível excluir o cliente. Existem resultados vinculados." }}}|Tentativa de excluir cliente com vínculo na tabela {{cnd_resultado}}|
  |*ValidationException* (Bean Validation)|400 Bad Request|{{{ "error": "Campo 'cnpj' inválido ou ausente." }}}|Dados obrigatórios ausentes ou inválidos|
  |*EmpresaVinculoObrigatorioException*|400 Bad Request|{{{ "error": "É necessário informar uma empresa válida (fk_empresa)." }}}|Tentativa de cadastrar cliente sem empresa vinculada|
  |*ConstraintViolationException* (Banco)|409 Conflict|{{{ "error": "Violação de chave única ou integridade referencial." }}}|Dados duplicados ou violação de foreign key|
  |*InternalServerErrorException*|500 Internal Server Error|{{{ "error": "Erro interno no servidor. Tente novamente mais tarde." }}}|Erros inesperados não tratados|
  |*SAAMIntegrationException*|502 Bad Gateway|{{{ "error": "Erro na comunicação com o SAAM. Verifique o serviço externo." }}}|Falha na integração com o SAAM|

  ----

  {panel:bgColor=#ffebe6}
  IGNORAR DAQUI PARA BAIXO….
  {panel}

  ----

  {panel:bgColor=#deebff}
  h3. *TASK: Criar API REST para gerenciamento de empresas e clientes (Spring Boot)*

  *Descrição:*

  Desenvolver uma API RESTful utilizando Spring Boot com suporte para operações CRUD na tabela {{cnd_cliente}}. Os dados da tabela {{cnd_empresa}} serão obtidos diretamente do SAAM e cadastrados apenas uma vez. Uma empresa pode ter vários clientes.
  {panel}

  h3. Objetivos:

  * Criar endpoints RESTful para {{cnd_cliente}}
  * Relacionar cliente com empresa
  * Cadastrar empresa automaticamente ao receber dados do SAAM
  * Implementar tratativas de erro e status HTTP adequados
  * Utilizar boas práticas (camadas: controller, service, repository, DTO, exception)



  h4. Endpoints

  ||Método||Endpoint||Ação||Corpo da Requisição||
  |POST|{{/clientes}}|Inserir novo cliente|{{{ "cnpj": "...", "periodicidade": ..., "status_cliente": "...", "nacional": true, "municipal": false, "estadual": true, "fk_empresa": 1 }}}|
  |PUT|{{/clientes/{id}}}|Atualizar cliente|Mesmo formato do POST|
  |DELETE|{{/clientes/{id}}}|Excluir cliente|Nenhum corpo necessário|
  |GET|{{/clientes}}|Listar clientes|Nenhum corpo necessário|

  ----

  {panel:bgColor=#deebff}
  Banco de dados:

  {noformat}CREATE TABLE cnd_empresa (
      id INT PRIMARY KEY,
      cnpj VARCHAR(18) NOT NULL,
      nome_empresa VARCHAR(255),
      id_empresa VARCHAR(6),
      status_empresa VARCHAR(50)
  );
  CREATE TABLE cnd_cliente (
      id SERIAL PRIMARY KEY,
      cnpj VARCHAR(18),
      periodicidade INT,
      status_cliente VARCHAR(50),
      nacional BOOLEAN,
      municipal BOOLEAN,
      estadual BOOLEAN,
      fk_empresa INT,
      FOREIGN KEY (fk_empresa) REFERENCES cnd_empresa(id)
  );
  CREATE TABLE cnd_resultado (
      id INT PRIMARY KEY,
      data_processamento TIMESTAMP,
      arquivo TEXT,
      situacao VARCHAR(50),
      fk_cliente INT,
      FOREIGN KEY (fk_cliente) REFERENCES cnd_cliente(id)
  );{noformat}
  {panel}


======================================================================
Ticket ID: PEC-4923
Título:    Criar estrutura da API para conectar com o SAAM-CR
--------------------
Descrição:

  {panel:bgColor=#eae6ff}
  h2. 🔍 *DESCRIÇÃO DE NEGÓCIO (OBJETIVO)*

  Atualmente, não há um mecanismo automatizado para controlar, de forma segura, se um cliente está autorizado a acessar a plataforma ou consumir os serviços disponíveis nos endpoints do SAAM. Essa ausência de validação expõe riscos operacionais, permitindo que clientes inativos ou com pendências tenham acesso indevido.

  Para resolver esse problema, será desenvolvido um recurso na API-CND que realizará a *validação do status de acesso do cliente*, integrando diretamente com o robô do José.

  Sempre que um cliente tentar consumir os endpoints, a API realizará uma chamada de validação para o seguinte endpoint externo:

  {noformat}http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById/GLSAAM?attribute=situacao{noformat}

  Nessa chamada, será enviado como parâmetro o {{IDCLIENTE}}. O robô irá retornar a situação do cliente. Caso a *situação seja igual a "1"*, o acesso será autorizado. Para qualquer outro valor, o cliente terá o acesso negado, impedindo login e qualquer consumo da API.

  ----

  h2. ✅ *CRITÉRIO DE ACEITE*

  * A API deve se conectar ao endpoint externo:
  {noformat}http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById/GLSAAM?attribute=situacao {noformat}
  O parâmetro {{IDCLIENTE}} deve ser enviado na chamada.
  * Se o retorno da consulta for:
  ** {{"situacao" = 1}} → *Acesso liberado*.
  ** Qualquer outro valor ({{0}}, {{2}}, {{3}}, {{null}}...) → *Acesso negado*.
  * A validação deve ser executada:
  ** No momento do login do cliente na plataforma.
  ** Antes do consumo de qualquer endpoint da API.
  * Se o cliente não estiver autorizado, o sistema deve:
  ** Retornar código *403 Forbidden*.
  ** Exibir uma mensagem clara:

  {code:json}{ "error": "Acesso negado. Cliente sem autorização ativa." }{code}

  A API deve lidar com falhas na integração (timeout, erro 5xx, erro de conexão), retornando:

  * Código *503 Service Unavailable*.
  * Mensagem:

  {code:json}{ "error": "Serviço de validação indisponível. Tente novamente mais tarde." }{code}

  h2. *REGRAS DE NEGÓCIO*

  * A validação da situação do cliente é *obrigatória* para qualquer tentativa de login ou consumo dos serviços da API.
  * Apenas clientes com {{situacao = 1}} podem acessar a plataforma e seus endpoints.
  * O parâmetro {{IDCLIENTE}} deve ser válido e existente.
  * Em caso de falha na comunicação com o robô do José (timeout, indisponibilidade ou erro de resposta), o acesso deve ser bloqueado por segurança, e uma mensagem amigável deve ser exibida ao cliente.
  * A verificação deve ser executada *em tempo real*, não podendo ser cacheada, garantindo que a situação mais recente do cliente seja sempre consultada.


  {panel}

  h2. *MAPEAMENTO DE EXCEÇÕES*

  ||*Exceção*||*Status HTTP*||*Mensagem Retorno (Exemplo)*||*Cenário*||
  |*ClienteNaoAutorizadoException*|403 Forbidden|{{{ "error": "Acesso negado. Cliente sem autorização ativa." }}}|Cliente com situação diferente de {{1}}|
  |*ClienteIdInvalidoException*|400 Bad Request|{{{ "error": "IDCLIENTE inválido ou não informado." }}}|Parâmetro {{IDCLIENTE}} ausente ou mal formatado|
  |*ServicoValidacaoIndisponivelException*|503 Service Unavailable|{{{ "error": "Serviço de validação indisponível. Tente novamente mais tarde." }}}|Timeout, erro 5xx ou falha na comunicação com o robô|
  |*InternalServerErrorException*|500 Internal Server Error|{{{ "error": "Erro interno no servidor. Tente novamente mais tarde." }}}|Erros inesperados|

  ----

  {panel:bgColor=#ffebe6}
  Ignorar a parte de baixo:
  {panel}

  ----

  {panel:bgColor=#deebff}
  Estruturar a API para conectar com o robô do José.
  Fazer a conexão da API

  A conexão, precisa passar como parâmetro no enpoint da API o IDCLIENTE para sabermos se ele está com o acesso liberado.

  ----

  * [http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById/GLSAAM?attribute=situacao|http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById/GLSAAM?attribute=situacao]
  ** Se a situação for = 1, cliente autorizado.
  ** Para as demais situações, não permitir o  login do cliente na plataforma, ou acesso aos endpoints.
  {panel}


======================================================================
Ticket ID: PEC-4869
Título:    Implementar consulta automática da CND Federal
--------------------
Descrição:

  {panel:bgColor=#e3fcef}
  h3. *Contexto do problema*

  O sistema SAAM atualmente não possui integração para obtenção da *Certidão Negativa de Débitos (CND) Federal*, emitida pela Receita Federal. Esse documento é fundamental para validar a regularidade fiscal de empresas durante processos de auditoria, análises de risco e conformidade. Hoje, a verificação é feita manualmente no site da Receita [https://solucoes.receita.fazenda.gov.br/servicos/certidaointernet/pj/emitir|https://solucoes.receita.fazenda.gov.br/servicos/certidaointernet/pj/emitir|smart-link] , o que torna o processo lento, propenso a erros e sem registro automatizado no sistema.
  {panel}

  {panel:bgColor=#deebff}
  _Como auditor ou analista no SAAM,_
  _Quero consultar automaticamente a CND Federal de uma empresa via Receita Federal,_
  _Para garantir agilidade, precisão e rastreabilidade na verificação da regularidade fiscal._
  {panel}

  {panel:bgColor=#eae6ff}
  h3. *Requisitos*

  * A funcionalidade deve realizar a consulta da CND Federal utilizando o site oficial da Receita Federal:
  [https://solucoes.receita.fazenda.gov.br/servicos/certidaointernet/pj/emitir|https://solucoes.receita.fazenda.gov.br/servicos/certidaointernet/pj/emitir|smart-link]
  * O CNPJ da empresa deve ser o único dado necessário para iniciar a consulta.
  * As seguintes informações devem ser extraídas da certidão (quando disponível):
  ** Situação da certidão (ex: negativa, positiva, positiva com efeitos de negativa)
  ** Data de emissão
  ** Data de validade
  ** Código de controle
  * Em caso de indisponibilidade da certidão, erro no site ou bloqueio de acesso, deve-se registrar o erro e sinalizar para análise manual, ou dar opções de realizar uma retentativa (retry de consulta).
  * As informações obtidas devem ser armazenadas no banco de dados e vinculadas ao respectivo CNPJ para histórico e auditoria.
  * A funcionalidade deve estar disponível via interface do SAAM (módulo de consultas) e também exposta por API interna.
  * A implementação deve respeitar os termos de uso da Receita Federal. Se houver captcha ou proteção anti-bot, deve-se considerar alternativas viáveis (ex: OCR, integração via serviço autorizado ou spike técnico).
  * Cada consulta deve registrar: CNPJ pesquisado, data e hora da requisição, status do retorno, e dados extraídos.
  {panel}


======================================================================
Ticket ID: PEC-4630
Título:    Sincronização Automática para CNDs - Tocantins - REQUER QUEBRA DE CAPTCHA
--------------------
Descrição:

  {panel:bgColor=#deebff}
  Como usuário SAAM,
  Quero que o sistema realize a sincronização automática das CNDs para os contribuintes do Tocantins,
  Para que eu possa consultar a situação fiscal com base no estado informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos:*

  * *Sincronização Automática:* O sistema deve acessar o site: [https://www.sefaz.to.gov.br/cnd/com.cnd.hecwbcnd01|https://www.sefaz.to.gov.br/cnd/com.cnd.hecwbcnd01|smart-link]
  * Consultar o status da CND utilizando o CPF/CNPJ informado no cadastro.
  * *Atualização no SAAM:*
  Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O botão para download deve ser habilitado apenas se a consulta e sincronização tiverem sido realizadas com sucesso.
  * *Status de Consulta:*
  Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4629
Título:    Sincronização Automática para CNDs - Minas Gerais - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  Como usuário SAAM,
  Quero que o sistema realize a sincronização automática das CNDs para os contribuintes de Minas Gerais,
  Para que eu possa consultar a situação fiscal com base no estado informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos:*

  * *Sincronização Automática:*
  O sistema deve acessar o site: [https://www2.fazenda.mg.gov.br/sol/ctrl/SOL/CDT/SERVICO_829?ACAO=INICIAR|https://www2.fazenda.mg.gov.br/sol/ctrl/SOL/CDT/SERVICO_829?ACAO=INICIAR|smart-link]
  * Consultar o status da CND utilizando o CPF/CNPJ informado no cadastro.
  * *Atualização no SAAM:*
  Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O botão para download deve ser habilitado apenas se a consulta e sincronização tiverem sido realizadas com sucesso.
  * *Status de Consulta:*
  Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4628
Título:    Sincronização Automática para CNDs - Maranhão - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  Como usuário SAAM,
  Quero que o sistema realize a sincronização automática das CNDs para os contribuintes do Maranhão,
  Para que eu possa consultar a situação fiscal com base no estado informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos:*

  * *Sincronização Automática:*
  O sistema deve acessar o site: [https://sistemas1.sefaz.ma.gov.br/certidoes/jsp/emissaoCertidaoNegativa/emissaoCertidaoNegativa.jsf|https://sistemas1.sefaz.ma.gov.br/certidoes/jsp/emissaoCertidaoNegativa/emissaoCertidaoNegativa.jsf]
  * Consultar o status da CND utilizando o CPF/CNPJ informado no cadastro.
  * *Atualização no SAAM:*
  Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O botão para download deve ser habilitado apenas se a consulta e sincronização tiverem sido realizadas com sucesso.
  * *Status de Consulta:*
  Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4627
Título:    Sincronização Automática para CNDs - Goiás - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  Como usuário SAAM,
  Quero que o sistema realize a sincronização automática das CNDs para os contribuintes de Goiás,
  Para que eu possa consultar a situação fiscal com base no estado informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos:*

  * *Sincronização Automática:*
  O sistema deve acessar o site: [https://www.go.gov.br/servicos-digitais/economia/emitir-certidao-negativa-de-debitos/emitir-certidao-negativa|https://www.go.gov.br/servicos-digitais/economia/emitir-certidao-negativa-de-debitos/emitir-certidao-negativa|smart-link]
  * Consultar o status da CND utilizando o CPF/CNPJ informado no cadastro.
  * *Atualização no SAAM:*
  Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O botão para download deve ser habilitado apenas se a consulta e sincronização tiverem sido realizadas com sucesso.
  * *Status de Consulta:*
  Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}

  ----


======================================================================
Ticket ID: PEC-4626
Título:    Sincronização Automática para CNDs - Distrito Federal - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  Como usuário SAAM,
  Quero que o sistema realize a sincronização automática das CNDs para os contribuintes do Distrito Federal,
  Para que eu possa consultar a situação fiscal com base no estado informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos:*


  * *Sincronização Automática:*
  O sistema deve acessar o site: [https://ww1.receita.fazenda.df.gov.br/cidadao/certidoes/Certidao|https://ww1.receita.fazenda.df.gov.br/cidadao/certidoes/Certidao|smart-link]
  * Consultar o status da CND utilizando o CPF/CNPJ informado no cadastro.
  * *Atualização no SAAM:*
  ** Preencher ou atualizar os seguintes campos da CND:
  *** Situação Fiscal
  *** Data de Emissão
  *** Data de Validade
  *** Órgão Emissor
  *** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O PDF deve ser associado ao botão:
  *** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  *** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  *** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.
  * *Status de Consulta:*
  ** Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  *** *Pendente:* Consulta em processamento. PDF indisponível para download.
  *** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  *** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  ** Enviar uma notificação no painel de atualizações do SAAM, informando:
  *** Consulta finalizada com sucesso e CND disponível.
  *** Ou falha, com detalhes do erro.


  {panel}


======================================================================
Ticket ID: PEC-4625
Título:    Sincronização Automática para CNDs - Bahia - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes da Bahia,
  *Para* que eu possa consultar a situação fiscal com base no estado informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos:*

  * *Sincronização Automática:*
  O sistema deve acessar o site: [https://servicos.sefaz.ba.gov.br/sistemas/DSCRE/Modulos/Publico/EmissaoCertidao.aspx|https://servicos.sefaz.ba.gov.br/sistemas/DSCRE/Modulos/Publico/EmissaoCertidao.aspx]
  * Consultar o status da CND utilizando o CPF/CNPJ informado no cadastro.
  * *Atualização no SAAM:*
  Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O botão para download deve ser habilitado apenas se a consulta e sincronização tiverem sido realizadas com sucesso.
  * *Status de Consulta:*
  Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.

  ----
  {panel}


======================================================================
Ticket ID: PEC-4624
Título:    Sincronização Automática para CNDs - Palmas (TO) - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes de Palmas (TO)
  *Para* que eu possa consultar a situação fiscal com base no município informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  *Sincronização Automática:*

  * O sistema deve acessar o site: [http://certidao.palmas.to.gov.br/cnd-pessoa/|http://certidao.palmas.to.gov.br/cnd-pessoa/]
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.
  * *Atualização no SAAM:*
  ** Preencher ou atualizar os seguintes campos da CND:
  *** Situação Fiscal
  *** Data de Emissão
  *** Data de Validade
  *** Órgão Emissor
  *** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O PDF deve ser associado ao botão:
  *** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  *** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  *** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.
  * *Status de Consulta:*
  ** Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  *** *Pendente:* Consulta em processamento. PDF indisponível para download.
  *** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  *** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  ** Enviar uma notificação no painel de atualizações do SAAM, informando:
  *** Consulta finalizada com sucesso e CND disponível.
  *** Ou falha, com detalhes do erro.
  {panel}

  ----

  {panel:bgColor=#deebff}
  Validar finalizade
  {panel}


======================================================================
Ticket ID: PEC-4623
Título:    Sincronização Automática para CNDs - Marabá (PA) - SITE INVÁLIDO
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes de Marabá (PA),
  *Para* que eu possa consultar a situação fiscal com base no município informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  *Sincronização Automática:*

  * O sistema deve acessar o site: [https://nfe.maraba.pa.gov.br/esiat/Certidao_Index.aspx|https://nfe.maraba.pa.gov.br/esiat/Certidao_Index.aspx].
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.
  * *Atualização no SAAM:*
  ** Preencher ou atualizar os seguintes campos da CND:
  *** Situação Fiscal
  *** Data de Emissão
  *** Data de Validade
  *** Órgão Emissor
  *** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O PDF deve ser associado ao botão:
  *** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  *** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  *** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.
  * *Status de Consulta:*
  ** Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  *** *Pendente:* Consulta em processamento. PDF indisponível para download.
  *** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  *** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  ** Enviar uma notificação no painel de atualizações do SAAM, informando:
  *** Consulta finalizada com sucesso e CND disponível.
  *** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4622
Título:    Sincronização Automática para CNDs - Benevides (PA) - REQUER QUEBRA DE CAPTCHA
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes de Benevides (PA),
  *Para* que eu possa consultar a situação fiscal com base no município informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  *Sincronização Automática:*

  * O sistema deve acessar o site: [https://benevides.desenvolvecidade.com.br/nfsd/pages/consulta/certidaoDebito/solicitacaoCertidaoDebito.jsf|https://benevides.desenvolvecidade.com.br/nfsd/pages/consulta/certidaoDebito/solicitacaoCertidaoDebito.jsf|smart-link]
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.
  * *Atualização no SAAM:*
  ** Preencher ou atualizar os seguintes campos da CND:
  *** Situação Fiscal
  *** Data de Emissão
  *** Data de Validade
  *** Órgão Emissor
  *** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O PDF deve ser associado ao botão:
  *** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  *** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  *** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.
  * *Status de Consulta:*
  ** Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  *** *Pendente:* Consulta em processamento. PDF indisponível para download.
  *** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  *** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  ** Enviar uma notificação no painel de atualizações do SAAM, informando:
  *** Consulta finalizada com sucesso e CND disponível.
  *** Ou falha, com detalhes do erro.

  ----
  {panel}


======================================================================
Ticket ID: PEC-4621
Título:    Sincronização Automática para CNDs - Uberlândia (MG) - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes de Uberlândia (MG),
  *Para* que eu possa consultar a situação fiscal com base no município informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  *Sincronização Automática:*

  * O sistema deve acessar o site: [http://portalsiat.uberlandia.mg.gov.br/dsf_udi_portal/inicial.do?evento=montaMenu&acronym=CERT_NEG|http://portalsiat.uberlandia.mg.gov.br/dsf_udi_portal/inicial.do?evento=montaMenu&acronym=CERT_NEG].
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.
  * *Atualização no SAAM:*
  ** Preencher ou atualizar os seguintes campos da CND:
  *** Situação Fiscal
  *** Data de Emissão
  *** Data de Validade
  *** Órgão Emissor
  *** Número da Certidão
  * *Disponibilização do Arquivo PDF:*
  ** Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  ** O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  ** O PDF deve ser associado ao botão:
  *** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  *** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  *** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.
  * *Status de Consulta:*
  ** Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  *** *Pendente:* Consulta em processamento. PDF indisponível para download.
  *** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  *** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).
  * *Notificação ao Usuário:*
  ** Enviar uma notificação no painel de atualizações do SAAM, informando:
  *** Consulta finalizada com sucesso e CND disponível.
  *** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4620
Título:    Sincronização Automática para CNDs - Patrocínio (MG) - NECESSITA UMA CONTA PARA VERIFICAR
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes de Patrocínio (MG),
  *Para* que eu possa consultar a situação fiscal com base no município informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  *Sincronização Automática:*

  * O sistema deve acessar o site: [https://patrocinio.simplissweb.com.br/contrib/Home/Index/1|https://patrocinio.simplissweb.com.br/contrib/Home/Index/1].
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.

  *Atualização no SAAM:*

  * Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão

  *Disponibilização do Arquivo PDF:*

  * Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  * O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  * O PDF deve ser associado ao botão:
  ** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  ** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  ** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.

  *Status de Consulta:*

  * Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).

  *Notificação ao Usuário:*

  * Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4619
Título:    Sincronização Automática para CNDs - Imperatriz (MA) - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs para os contribuintes de Imperatriz (MA),
  *Para* que eu possa consultar a situação fiscal com base no município informado no cadastro, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  *Sincronização Automática:*

  * O sistema deve acessar o site: [https://nfse-ma-imperatriz.portalfacil.com.br/|https://nfse-ma-imperatriz.portalfacil.com.br/].
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.

  *Atualização no SAAM:*

  * Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão

  *Disponibilização do Arquivo PDF:*

  * Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  * O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  * O PDF deve ser associado ao botão:
  ** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  ** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  ** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.

  *Status de Consulta:*

  * Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).

  *Notificação ao Usuário:*

  * Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.

  ----
  {panel}


======================================================================
Ticket ID: PEC-4618
Título:    Sincronização Automática para CNDs - Senador Canedo (GO) - SITE INVÁLIDO
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs de contribuintes de Senador Canedo (GO),
  *Para* que eu possa consultar a situação fiscal, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  h5. *Sincronização Automática:*

  * O sistema deve acessar o site: [http://45.65.223.34:5661/servicosweb/home.jsf|http://45.65.223.34:5661/servicosweb/home.jsf].
  * Consultar o status da CND utilizando CPF/CNPJ.

  h5. *Atualização no SAAM:*

  * Preencher ou atualizar os seguintes campos da CND:
  ** *Situação Fiscal*
  ** *Data de Emissão*
  ** *Data de Validade*
  ** *Órgão Emissor*
  ** *Número da Certidão*

  h5. *Disponibilização do Arquivo PDF:*

  * Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  * O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  * O PDF deve ser associado ao botão.
  ** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  ** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  ** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.

  h5. *Status de Consulta:*

  * Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).

  h5. *Notificação ao Usuário:*

  * Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4617
Título:    Sincronização Automática para CNDs - Brasília (DF) - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs de contribuintes de Brasília (DF),
  *Para* que eu possa consultar a situação fiscal, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  h5. *Sincronização Automática:*

  * O sistema deve acessar o site: [https://ww1.receita.fazenda.df.gov.br/cidadao/certidoes/Certidao|https://ww1.receita.fazenda.df.gov.br/cidadao/certidoes/Certidao].
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.

  h5. *Atualização no SAAM:*

  * Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão

  h5. *Disponibilização do Arquivo PDF:*

  * Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  * O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  * O PDF deve ser associado ao botão:
  ** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  ** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  ** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.

  h5. *Status de Consulta:*

  * Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).

  h5. *Notificação ao Usuário:*

  * Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}


======================================================================
Ticket ID: PEC-4616
Título:    Sincronização Automática para CNDs - São Sebastião do Passé (BA) - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o sistema realize a sincronização automática das CNDs de contribuintes de São Sebastião do Passé (BA),
  *Para* que eu possa consultar a situação fiscal, atualizar os dados no sistema e obter o arquivo PDF da certidão.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h4. Requisitos:

  h5. *Sincronização Automática:*

  * O sistema deve acessar o site: [https://saosebastiaodopasse.saatri.com.br/|https://saosebastiaodopasse.saatri.com.br/].
  * Consultar o status da CND utilizando o CPF/CNPJ e o município informado no cadastro.

  h5. *Atualização no SAAM:*

  * Preencher ou atualizar os seguintes campos da CND:
  ** Situação Fiscal
  ** Data de Emissão
  ** Data de Validade
  ** Órgão Emissor
  ** Número da Certidão

  h5. *Disponibilização do Arquivo PDF:*

  * Após a consulta, o SAAM deve salvar o arquivo PDF da CND gerado pelo site.
  * O PDF deve ser armazenado no SAAM e vinculado ao registro da CND correspondente, permitindo download e visualização pelo usuário.
  * O PDF deve ser associado ao botão:
  ** Conforme o usuário seleciona a linha da grid, deve ser permitido baixar o PDF da CND selecionada.
  ** O botão deve estar habilitado somente se a consulta e sincronização tiverem sido realizadas.
  ** Caso contrário, o botão PDF deve estar desabilitado, impossibilitando o download.

  h5. *Status de Consulta:*

  * Atualizar a coluna "Status de Consulta" na lista de CNDs com os seguintes estados:
  ** *Pendente:* Consulta em processamento. PDF indisponível para download.
  ** *Concluída:* Dados atualizados e PDF disponível no SAAM. PDF disponível para download.
  ** *Erro:* Falha na consulta (ex.: site inacessível ou limite de consultas atingido).

  h5. *Notificação ao Usuário:*

  * Enviar uma notificação no painel de atualizações do SAAM, informando:
  ** Consulta finalizada com sucesso e CND disponível.
  ** Ou falha, com detalhes do erro.
  {panel}

  ----


======================================================================
Ticket ID: PEC-4539
Título:    Sincronização Automática para CNDs - Camaçari (BA) - OK
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,
  *Quero* que o seja possível realizar a sincronização automática das CNDs de contribuintes de Camaçari (BA),
  *Para* que eu possa consultar a situação fiscal nos links específicos e atualizar as informações automaticamente.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos*

  h2. *📌 Descrição*

  *Como um usuário da API do SAAM Auditoria*,
  *Quero consultar automaticamente a Certidão Negativa de Débitos (CND) do site da Prefeitura de Camaçari (BA),*
  *Para obter informações completas sobre a regularidade fiscal de contribuintes e automatizar a sincronização desses dados no meu sistema.*

  ----

  h2. *🚀 Requisitos da API*

  h3. *🔹 1. Consulta Manual via API*

  A API permitirá a consulta *manual* da CND de Camaçari (BA), retornando *todos os detalhes disponíveis*.

  h4. *📌 Endpoint:*

  {code:json}GET /saam/cnd/camacari/{cpf_cnpj}{code}



  h4. *📌 Parâmetros:*

  * {{cpf_cnpj}} → CPF ou CNPJ do contribuinte a ser consultado.

  h4. *📌 Fluxo da API:*

  # A API acessa o site da *Prefeitura de Camaçari (BA)*:
  #* *URL:* [https://sefazweb.camacari.ba.gov.br/prefeituras/login.do|https://sefazweb.camacari.ba.gov.br/prefeituras/login.do]
  # *Autenticação* → Utiliza usuário e senha fornecidos pelo cliente.
  # *Consulta da CND* → Realiza a busca pelo CPF/CNPJ.
  # *Retorna todas as informações disponíveis* da Certidão Negativa de Débitos.

  h4. *📌 Exemplo de Resposta (Sucesso - 200 OK)*

  {code:json}{
      "status": "success",
      "data": {
          "cpf_cnpj": "12.345.678/0001-99",
          "nome_contribuinte": "Empresa Exemplo LTDA",
          "situacao_fiscal": "Regular",
          "motivo_irregularidade": null,
          "data_emissao": "2024-03-10",
          "data_validade": "2025-03-10",
          "orgao_emissor": "Prefeitura Municipal de Camaçari",
          "numero_certidao": "123456789",
          "inscricao_municipal": "987654321",
          "endereco_contribuinte": "Rua Exemplo, 123, Camaçari - BA",
          "natureza_juridica": "Sociedade Limitada",
          "atividade_economica": "Comércio Varejista",
          "quantidade_dividas": 0,
          "pdf_url": "https://sefazweb.camacari.ba.gov.br/cnd/123456789.pdf"
      }
  }{code}

  📌 Exemplo de Resposta (Erro - 404 Not Found)

  {code:json}{
      "status": "error",
      "message": "CND não encontrada para o CPF/CNPJ informado."
  }{code}

  h3. *🔹 2. Consulta Automática (Configuração pelo Cliente)*

  A API permitirá *configurar a sincronização automática* da CND.

  h4. *📌 Endpoint para Configuração Automática*

  {code:json}POST /saam/config/sync/cnd/camacari{code}

  📌 Parâmetros:

  {code:json}{
      "cpf_cnpj": "12.345.678/0001-99",
      "frequencia": "diaria"  // Opções: "diaria", "semanal", "mensal"
  }{code}

  h4. *📌 Funcionamento:*

  * O cliente ativa a *sincronização automática* para um contribuinte específico.
  * A API consulta a CND *automaticamente* conforme a frequência definida.
  * O sistema recebe *notificações via webhook* com os novos dados.

  h4. *📌 Exemplo de Webhook Enviado pela API:*

  {code:json}{
      "evento": "sincronizacao_cnd",
      "cpf_cnpj": "12.345.678/0001-99",
      "nome_contribuinte": "Empresa Exemplo LTDA",
      "situacao_fiscal": "Irregular",
      "motivo_irregularidade": "Débitos em aberto com a prefeitura",
      "data_emissao": "2024-03-10",
      "data_validade": "2025-03-10",
      "numero_certidao": "123456789",
      "pdf_url": "https://sefazweb.camacari.ba.gov.br/cnd/123456789.pdf"
  }{code}

  h3. *🔹 3. Autenticação e Segurança*

  * O acesso à API será protegido por *API Key* para cada cliente.
  * A autenticação no site da *Prefeitura de Camaçari* será feita via *usuário e senha*, armazenados com *segurança*.
  * Todas as requisições serão realizadas *via HTTPS*.

  h4. *📌 Endpoint para Gerar API Key*

  {code:json}POST /saam/auth/generate-key{code}

  📌 Parâmetros:

  {code:json}{
      "email": "usuario@email.com",
      "senha": "123456"
  }{code}

  📌 Resposta:

  {noformat}{
      "api_key": "abcdef1234567890"
  }{noformat}

  A *API Key* será necessária para acessar os endpoints de consulta.

  ----

  h2. *🎯 Critérios de Aceitação*

  ✅ A API acessa o site da *Prefeitura de Camaçari* e realiza a autenticação.
  ✅ A API consulta a *CND do contribuinte* pelo CPF/CNPJ e retorna o máximo de informações disponíveis.
  ✅ O cliente pode *ativar a sincronização automática* das CNDs.
  ✅ O sistema *envia webhooks* quando há novas informações.
  ✅ *Autenticação segura* com *API Key* para garantir a privacidade dos dados.
  {panel}



  {panel:bgColor=#fffae6}
  *Validar como vai ser a sincronização, se vai ser por botão ou por tempo (interno)*

  Pegar com o cliente o  login do site.
  {panel}


======================================================================
Ticket ID: PEC-4538
Título:    CND - Excluir dados tela principal
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como usuário SAAM*, quero poder excluir uma Certidão Negativa de Débitos (CND) cadastrada incorretamente ou que não seja mais necessária, garantindo a remoção segura do registro do sistema.
  {panel}

  {panel:bgColor=#eae6ff}
  h3. Requisitos

  # A tela de consulta deve exibir uma opção de exclusão para cada CND listada.
  # *Confirmação de Exclusão*
  #* Ao clicar para excluir, deve ser exibido uma confirmação para evitar exclusões acidentais:
  #** Mensagem: “Tem certeza de que deseja excluir a CND XXXX (CPF ou CNPJ da CND)?
  # *Remoção do Registro*
  #* Após a confirmação, a CND deve ser removida.
  #* A lista de CNDs é atualizada para refletir a exclusão.
  #** Dar o reload na tela para não aparecer como “falso positivo” na lista.
  # Deve permitir a exclusão de múltipla e única das CNDs.
  {panel}


======================================================================
Ticket ID: PEC-4537
Título:    CND - Editar dados tela principal
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM,

  *Quero* poder editar os dados de uma CND já cadastrada,

  *Para* que eu possa fazer a atualização das informações inseridas manualmente, exceto CPF/CNPJ.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  h3. Requisitos

  h4. *Campos Permitidos para Edição:*

  * *Tipo de Certidão:* Permitir alterar entre Municipal, Estadual e Federal.
  * *Situação Fiscal:* Permitir alterar entre Regular e Irregular.
  * *Nome do Contribuinte:* Campo editável para correção de informações inseridas manualmente.
  * *Órgão Emissor:* Permitir alterar o órgão responsável (Municipal, Estadual ou Federal).
  * *Data de Emissão, Data de Validade, e Data da Última Consulta:* Campos editáveis para ajustes em datas registradas erroneamente.
  * *Atividade Econômica (CNAE):* Permitir editar ou inserir o código, se necessário.
  * *Número da Certidão:* Permitir corrigir o número, caso tenha sido inserido de forma incorreta.
  * *Observações:* Permitir editar ou adicionar informações adicionais relevantes.

  h4. *Campos Bloqueados para Edição:*

  * *CPF/CNPJ:* O identificador do contribuinte não pode ser alterado após o registro.
  * *Município:* Bloqueado para evitar inconsistências com o órgão emissor.

  h4. *Validações:*

  * *Edição única:* Permitir apenas a edição de um registro por vez.
  * *Proibição de edição em lote:* Não permitir que múltiplos registros sejam editados ao mesmo tempo.
  {panel}

  ----

  {panel:bgColor=#fffae6}
  Validar com a diretoria sobre essa trava que impacta diretamente em questão financeira.
  Não permitir realizar a edição do CPF/CPNJ, implica que o cliente contrate mais licenças, resultando em mais caixa.
  {panel}


======================================================================
Ticket ID: PEC-4536
Título:    CND - Criar tela principal
--------------------
Descrição:

  {panel:bgColor=#deebff}
  *Como* usuário SAAM

  *Quero* ter acesso a uma tela de cadastro e consulta de Certidões Negativas de Débitos (CND),

  *Para que* eu possa registrar, consultar e verificar a situação fiscal de contribuintes nos âmbitos municipal, estadual e federal.
  {panel}

  ----

  {panel:bgColor=#eae6ff}
  *Requisitos*

  * Criar uma tela que permita o cadastro de uma nova CND, com os seguintes campos:
  *# *Status:* Status para habilitar/desabilitar
  *## Opção para habilitar/desabilitar a consulta.
  *# *Tipo de Certidão:*
  *#* Opções de seleção única: Municipal, Estadual e Federal.
  *# *Município:*
  *#* Campo referente ao município que será consultado.
  *#** {color:#ff5630}_Pode deixar inicialmente dados mockados inicialmente com uma lista futura da API para disponibilizar municípios de consulta._{color}
  *# *Número da Certidão:*
  *#* Campo numérico único para identificar a certidão.
  *# *Nome do Contribuinte:*
  *#* Campo obrigatório (texto), preenchido manualmente pelo usuário.
  *# *CPF ou CNPJ do Contribuinte:*
  *#* Permitir a inserção de um dos dois, com validação conforme o tipo de documento (tamanho e formato).
  *#** Validar se é CNPJ ou CPF
  *# *Órgão Emissor:*
  *#* Campo de seleção conforme o tipo de certidão:
  *#** Municipal → Prefeitura Municipal.
  *#** Estadual → Secretaria da Fazenda do Estado.
  *#** Federal → Receita Federal do Brasil.
  *# *Data de Emissão:*
  *#* Campo de data, preenchido automaticamente pelo SAAM.
  *# *Data de Validade:*
  *#* Campo de data, preenchido com os dados vindos da API.
  *# *Data da Última Consulta:*
  *#* Campo de data hora, preenchido automaticamente com a data hora now();.
  *# *Atividade Econômica (CNAE):*
  *#* Campo opcional para inserir o código CNAE, se aplicável.
  *#** Verificar 12.8.
  *# *Observações:*
  *#* Campo opcional para inserir comentários ou informações adicionais.

  ----

  A tela deve exibir uma lista de todas as CNDs registradas, com as seguintes funcionalidades:

  * *Paginação:* Conforme padrão SAAM.
  * *Busca e Filtros:* Permitir filtros para pesquisar.
  * *Botões de Exportação:* Permitir exportar dados em formatos PDF e Excel.
  * *Ações na Linha:* Botões para editar, renovar ou excluir CND diretamente na linha da tabela.

  ----
  {panel}

  ----

  {panel:bgColor=#fffae6}
  Validar com Yuri sobre onde vai ficar o acesso a rotina.
  {panel}


======================================================================
Ticket ID: PEC-4535
Título:    Projeto CND
--------------------
Descrição:



  [https://youtu.be/IGIRYZ5CydU|https://youtu.be/IGIRYZ5CydU|smart-embed]

  ----

  {panel:bgColor=#deebff}
  A Certidão Negativa de Débitos (CND) é um documento oficial, essencial para comprovar a situação fiscal de uma pessoa física ou jurídica perante os órgãos públicos. Esse documento pode ser emitido em três âmbitos: *estadual*, *municipal* e *federal*, cada um cobrindo obrigações e tributos específicos.

  A CND apresenta a *situação* do contribuinte, que pode ser *regular* (sem débitos pendentes) ou *irregular* (com débitos existentes), impactando o acesso do contribuinte a processos de licitação, financiamentos, concessão de benefícios, entre outros. Para obter uma CND regular, o contribuinte deve estar em conformidade com suas obrigações fiscais.

  A certidão tem uma *data de validade*, pois, passado um determinado período, é necessário verificar novamente a situação fiscal do contribuinte para garantir que ele permaneça regular. Além disso, a *data de consulta* registra o momento exato em que a situação fiscal foi verificada, sendo essencial para auditorias e validações do documento.

  Para facilitar o acesso e validação do documento, a CND frequentemente está disponível em *PDF*, o que permite ao contribuinte ou terceiros interessados (como bancos, órgãos governamentais e parceiros comerciais) acessar a certidão digitalmente.

  Esses dados estruturados em um sistema de gestão de CND tornam mais eficiente o processo de consulta e renovação, garantindo que contribuintes e empresas possam acompanhar e manter sua regularidade fiscal de forma prática e centralizada.
  {panel}


======================================================================
Ticket ID: MSA-120
Título:    Estudo de caso âmbito Estadual ->  Goiás
--------------------
Descrição:

  {panel:bgColor=#e3fcef}
  No âmbito estadual, a Certidão Negativa de Débitos (CND) é emitida pelas Secretarias de Fazenda de cada estado. Este documento atesta que o contribuinte não possui débitos pendentes relativos a tributos estaduais, como o ICMS (Imposto sobre Circulação de Mercadorias e Serviços), IPVA (Imposto sobre a Propriedade de Veículos Automotores), entre outros. É um documento fundamental para a participação em licitações estaduais, obtenção de financiamentos e outros procedimentos que exigem a comprovação de regularidade fiscal.

  ----

  h3. Estrutura da CND Estadual

  h4. Cabeçalho

  * *Logotipo da Secretaria da Economia do Estado de Goiás*
  * *Nome do Órgão Emissor*: Secretaria da Economia do Estado de Goiás.
  * *Título*: Certidão Negativa de Débitos.

  h4. 2. Dados do Contribuinte

  * *Nome/Razão Social*: Nome completo da pessoa física ou jurídica.
  * *Número de Inscrição*: CPF para pessoas físicas ou CNPJ para pessoas jurídicas.
  * *Endereço*: Endereço completo do contribuinte.

  h4. 3. Número da Certidão

  * *Código Único de Identificação*: Um número único que identifica a certidão emitida.

  h4. 4. Declaração de Situação Fiscal

  * *Descrição*: Declaração de que o contribuinte não possui débitos pendentes ou em aberto relativos aos tributos estaduais.

  h4. 5. Data de Emissão

  * *Data*: A data em que a certidão foi emitida.

  h4. 6. Validade

  * *Período de Validade*: A validade da certidão, que geralmente é de 90 dias.

  h4. 7. Legislação e Fundamentação Legal

  * *Referências Legais*: Citação das leis, normas ou regulamentos que fundamentam a emissão da CND.

  h4. 8. Assinatura e Autenticação

  * *Assinatura*: Assinatura do responsável pelo órgão emissor, ou assinatura digital no caso de certidões eletrônicas.
  * *Código de Autenticação*: Código para verificação da autenticidade da certidão online.

  h3. Exemplo de Estrutura da CND Estadual para Goiás

  h3.

  {noformat}-----------------------------------------------------------
  |      Governo do Estado de Goiás                         |
  |      Secretaria da Economia                             |
  |      Certidão Negativa de Débitos                       |
  -----------------------------------------------------------
  | Contribuinte:                                           |
  | Nome: Empresa XYZ Ltda.                                 |
  | CNPJ: 12.345.678/0001-90                                |
  | Endereço: Rua Exemplo, 123, Bairro, Cidade, GO          |
  -----------------------------------------------------------
  | Número da Certidão: 9876543210                          |
  -----------------------------------------------------------
  | Declaração:                                             |
  | Esta certidão atesta que a Empresa XYZ Ltda. não possui |
  | débitos pendentes perante a Secretaria da Economia do   |
  | Estado de Goiás até a presente data.                    |
  -----------------------------------------------------------
  | Data de Emissão: 15/07/2024                             |
  | Validade: 90 dias                                       |
  -----------------------------------------------------------
  | Legislação:                                             |
  | Esta certidão é emitida conforme o Art. XYZ da Lei ABC. |
  -----------------------------------------------------------
  | Assinatura:                                             |
  | [Assinatura Digital / Código de Autenticação]           |
  -----------------------------------------------------------{noformat}

  ----

  h3. Processo de Emissão da CND Estadual para Goiás

  # *Solicitação*:
  #* O contribuinte deve solicitar a certidão no site da Secretaria da Economia do Estado de Goiás, acessando a área de serviços online ou presencialmente em um posto de atendimento.
  #* É necessário fornecer dados como CPF ou CNPJ e outros documentos que possam ser exigidos.
  # *Verificação*:
  #* A Secretaria da Economia realiza uma verificação da situação fiscal do contribuinte.
  #* Verifica-se se não há débitos pendentes relativos aos tributos estaduais.
  # *Emissão*:
  #* Se não houver débitos pendentes, a CND é emitida.
  #* A certidão pode ser impressa ou acessada eletronicamente, dependendo do sistema da Secretaria da Economia.

  h3. Considerações Importantes

  * *Regularidade Fiscal*: Manter a regularidade fiscal é essencial para evitar problemas na obtenção da CND.
  * *Monitoramento*: Verificar regularmente a situação fiscal no âmbito estadual ajuda a evitar surpresas.
  * *Prazo de Validade*: Ficar atento ao prazo de validade da certidão, pois ela deve ser renovada periodicamente conforme necessário para continuar comprovando a regularidade fiscal.

  h3. Dicas para Emissão

  * *Documentação Necessária*: Ter em mãos todos os documentos necessários, como comprovantes de pagamento de tributos, para agilizar o processo de solicitação.
  * *Consulta Prévia*: Fazer uma consulta prévia no portal da Secretaria da Economia para verificar possíveis pendências e resolver antes de solicitar a certidão.
  * *Manter Registros Atualizados*: Garantir que todas as informações cadastrais e fiscais estejam atualizadas na Secretaria da Economia.

  h3. Tipos de Certidões Estaduais para Goiás

  Além da Certidão Negativa de Débitos, existem outras variações que podem ser emitidas dependendo da situação do contribuinte:

  * *Certidão Positiva com Efeitos de Negativa (CPEN)*: Emitida quando o contribuinte possui débitos, mas estes estão garantidos, parcelados ou com a exigibilidade suspensa.
  * *Certidão de Regularidade Fiscal*: Pode ser específica para certos tipos de tributos, como o ICMS.
  {panel}

  *Material complementar*
  Portal para consutla:[https://www.sefaz.go.gov.br/certidao/emissao/|https://www.sefaz.go.gov.br/certidao/emissao/|smart-link]

  Portal com informações gerais: [https://www.go.gov.br/servicos/servico/emitir-certidao-negativa-de-debitos--fazenda-estadual|https://www.go.gov.br/servicos/servico/emitir-certidao-negativa-de-debitos--fazenda-estadual|smart-link]

  Validar certidão: [https://www.sefaz.go.gov.br/Certidao/Validacao/default.asp|https://www.sefaz.go.gov.br/Certidao/Validacao/default.asp|smart-link]


======================================================================
Ticket ID: MSA-119
Título:    Estudo de caso ambito municipal - São Paulo
--------------------
Descrição:

  {panel:bgColor=#e3fcef}
  No âmbito municipal, o CND é um documento emitido pela prefeitura de cada município para comprovar que um contribuinte não possui débitos pendentes relativos a tributos municipais. Esses tributos podem incluir o Imposto Sobre Serviços (ISS), o Imposto Predial e Territorial Urbano (IPTU), taxas de licenciamento, entre outros.

  h3. Estrutura da CND Municipal

  h4. 1. Cabeçalho

  * *Logotipo da Prefeitura*
  * *Nome do Órgão Emissor*: Geralmente a Secretaria de Finanças ou o Departamento de Arrecadação e Tributos.
  * *Título*: Certidão Negativa de Débitos.

  h4. 2. Dados do Contribuinte

  * *Nome/Razão Social*: Nome completo da pessoa física ou jurídica.
  * *Número de Inscrição*: CPF para pessoas físicas ou CNPJ para pessoas jurídicas.
  * *Endereço*: Endereço completo do contribuinte.

  h4. 3. Número da Certidão

  * *Código Único de Identificação*: Um número único que identifica a certidão emitida.

  h4. 4. Declaração de Situação Fiscal

  * *Descrição*: Declaração de que o contribuinte não possui débitos pendentes junto à prefeitura no momento da emissão da certidão.

  h4. 5. Data de Emissão

  * *Data*: A data em que a certidão foi emitida.

  h4. 6. Validade

  * *Período de Validade*: A validade da certidão, que geralmente varia entre 30 a 180 dias, dependendo do município.

  h4. 7. Legislação e Fundamentação Legal

  * *Referências Legais*: Citação das leis, normas ou regulamentos que fundamentam a emissão da CND.

  h4. 8. Assinatura e Autenticação

  * *Assinatura*: Assinatura do responsável pelo órgão emissor, ou assinatura digital no caso de certidões eletrônicas.
  * *Código de Autenticação*: Código para verificação da autenticidade da certidão online.

  h3. Exemplo de Estrutura da CND Municipal

  h4. Prefeitura Municipal de São Paulo

  ----

  {noformat}-----------------------------------------------------------
  |          Prefeitura Municipal de São Paulo              |
  |          Secretaria de Finanças e Desenvolvimento       |
  |          Certidão Negativa de Débitos                   |
  -----------------------------------------------------------
  | Contribuinte:                                           |
  | Nome: Empresa XYZ Ltda.                                 |
  | CNPJ: 12.345.678/0001-90                                |
  | Endereço: Rua Exemplo, 123, Bairro, Cidade, Estado      |
  -----------------------------------------------------------
  | Número da Certidão: 1234567890                          |
  -----------------------------------------------------------
  | Declaração:                                             |
  | Esta certidão atesta que a Empresa XYZ Ltda. não possui |
  | débitos pendentes perante a Prefeitura Municipal de São |
  | Paulo até a presente data.                              |
  -----------------------------------------------------------
  | Data de Emissão: 15/07/2024                             |
  | Validade: 90 dias                                       |
  -----------------------------------------------------------
  | Legislação:                                             |
  | Esta certidão é emitida conforme o Art. XYZ da Lei ABC. |
  -----------------------------------------------------------
  | Assinatura:                                             |
  | [Assinatura Digital / Código de Autenticação]           |
  -----------------------------------------------------------{noformat}

  ----

  h3. Processo de Emissão da CND Municipal

  # *Solicitação*:
  #* O contribuinte deve solicitar a certidão na prefeitura ou no site da prefeitura, se disponível.
  #* É necessário fornecer dados como CPF ou CNPJ e outros documentos que possam ser exigidos.
  # *Verificação*:
  #* A prefeitura realiza uma verificação da situação fiscal do contribuinte.
  #* Verifica-se se não há débitos pendentes relativos aos tributos municipais.
  # *Emissão*:
  #* Se não houver débitos pendentes, a CND é emitida.
  #* A certidão pode ser impressa ou acessada eletronicamente, dependendo do sistema da prefeitura.

  h3. Considerações Importantes

  * *Regularidade Fiscal*: Manter a regularidade fiscal é essencial para evitar problemas na obtenção da CND.
  * *Monitoramento*: Verificar regularmente a situação fiscal no âmbito municipal ajuda a evitar surpresas.
  * *Prazo de Validade*: Ficar atento ao prazo de validade da certidão, pois ela deve ser renovada periodicamente conforme necessário para continuar comprovando a regularidade fiscal.

  h3. Dicas para Emissão

  * *Documentação Necessária*: Ter em mãos todos os documentos necessários, como comprovantes de pagamento de tributos, para agilizar o processo de solicitação.
  * *Consulta Prévia*: Fazer uma consulta prévia no portal da prefeitura para verificar possíveis pendências e resolver antes de solicitar a certidão.
  * *Manter Registros Atualizados*: Garantir que todas as informações cadastrais e fiscais estejam atualizadas na prefeitura.
  {panel}

  Material complementar:

  [https://www10.fazenda.sp.gov.br/CertidaoNegativaDeb/Pages/EmissaoCertidaoNegativa.aspx|https://www10.fazenda.sp.gov.br/CertidaoNegativaDeb/Pages/EmissaoCertidaoNegativa.aspx|smart-link]

  [https://www.youtube.com/watch?app=desktop&v=hMOUNX75MXw&ab_channel=LucianoSpindola|https://www.youtube.com/watch?app=desktop&v=hMOUNX75MXw&ab_channel=LucianoSpindola|smart-link]


======================================================================
Ticket ID: MSA-118
Título:    Estudo de caso âmbito Federal
--------------------
Descrição:

  {panel:bgColor=#deebff}
  Introdução:

  Certidões Negativas de Débitos (CNDs) são documentos emitidos pelos órgãos governamentais para comprovar que uma pessoa física ou jurídica não possui débitos ou pendências fiscais em um determinado âmbito. Elas são essenciais para diversas atividades, como participação em licitações, obtenção de empréstimos e regularização de contratos. Vamos detalhar cada tipo:

  h3. 1. CND Federal

  *Órgão Emissor*: Receita Federal do Brasil (RFB).

  *Descrição*:

  * A Certidão Negativa de Débitos Federais atesta a regularidade fiscal de uma empresa ou pessoa física perante a Receita Federal e a Procuradoria-Geral da Fazenda Nacional (PGFN).
  * Inclui tributos como Imposto de Renda, Contribuições Previdenciárias, PIS, COFINS, IPI, entre outros.

  *Processo de Emissão*:

  * Pode ser solicitada online no site da Receita Federal.
  * A consulta envolve a verificação de débitos e pendências fiscais e previdenciárias.
  * Se houver algum débito, será emitida uma Certidão Positiva com Efeitos de Negativa (CPEN) caso o débito esteja garantido ou parcelado.

  ----

  h3. Estrutura da CND Federal

  h4. 1. Cabeçalho

  * *Logotipo da Receita Federal do Brasil*
  * *Nome do órgão emissor*: Receita Federal do Brasil ou Procuradoria-Geral da Fazenda Nacional.
  * *Título*: Certidão Negativa de Débitos.

  h4. 2. Dados do Contribuinte

  * *Nome/Razão Social*: Nome completo da pessoa física ou jurídica.
  * *Número de Inscrição*: CPF para pessoas físicas ou CNPJ para pessoas jurídicas.
  * *Endereço*: Endereço completo do contribuinte.

  h4. 3. Número da Certidão

  * *Código Único de Identificação*: Um número único que identifica a certidão emitida.

  h4. 4. Declaração de Situação Fiscal

  * *Descrição*: Declaração de que o contribuinte não possui débitos pendentes ou em aberto relativos aos tributos federais.

  h4. 5. Data de Emissão

  * *Data*: A data em que a certidão foi emitida.

  h4. 6. Validade

  * *Período de Validade*: A validade da certidão, que geralmente é de 180 dias.

  h4. 7. Legislação e Fundamentação Legal

  * *Referências Legais*: Citação das leis, normas ou regulamentos que fundamentam a emissão da CND.

  h4. 8. Assinatura e Autenticação

  * *Assinatura*: Assinatura do responsável pelo órgão emissor, ou assinatura digital no caso de certidões eletrônicas.
  * *Código de Autenticação*: Código para verificação da autenticidade da certidão online.

  h3. Exemplo de Estrutura da CND Federal

  {noformat}-----------------------------------------------------------
  |          Receita Federal do Brasil                      |
  |          Certidão Negativa de Débitos                   |
  -----------------------------------------------------------
  | Contribuinte:                                           |
  | Nome: Empresa ABC Ltda.                                 |
  | CNPJ: 12.345.678/0001-90                                |
  | Endereço: Rua Exemplo, 123, Bairro, Cidade, Estado      |
  -----------------------------------------------------------
  | Número da Certidão: 1234567890                          |
  -----------------------------------------------------------
  | Declaração:                                             |
  | Esta certidão atesta que a Empresa ABC Ltda. não possui |
  | débitos pendentes perante a Receita Federal do Brasil e |
  | a Procuradoria-Geral da Fazenda Nacional até a presente |
  | data.                                                   |
  -----------------------------------------------------------
  | Data de Emissão: 15/07/2024                             |
  | Validade: 180 dias                                      |
  -----------------------------------------------------------
  | Legislação:                                             |
  | Esta certidão é emitida conforme o Art. XYZ da Lei ABC. |
  -----------------------------------------------------------
  | Assinatura:                                             |
  | [Assinatura Digital / Código de Autenticação]           |
  -----------------------------------------------------------{noformat}

  ----

  h3. Processo de Emissão da CND Federal

  # *Solicitação*:
  #* O contribuinte deve solicitar a certidão no site da Receita Federal, acessando o e-CAC (Centro Virtual de Atendimento ao Contribuinte).
  #* É necessário fornecer dados como CPF ou CNPJ e outros documentos que possam ser exigidos.
  # *Verificação*:
  #* A Receita Federal realiza uma verificação da situação fiscal do contribuinte.
  #* Verifica-se se não há débitos pendentes relativos aos tributos federais.
  # *Emissão*:
  #* Se não houver débitos pendentes, a CND é emitida.
  #* A certidão pode ser impressa ou acessada eletronicamente, dependendo do sistema da Receita Federal.

  ----

  h3. Tipos de Certidões Federais

  Além da Certidão Negativa de Débitos, existem outras variações que podem ser emitidas dependendo da situação do contribuinte:

  * *Certidão Positiva com Efeitos de Negativa (CPEN)*: Emitida quando o contribuinte possui débitos, mas estes estão garantidos, parcelados ou com a exigibilidade suspensa.
  * *Certidão Negativa de Débitos Trabalhistas (CNDT)*: Certidão específica para comprovar a regularidade com as obrigações trabalhistas.
  {panel}

  *Material complementar*

  Site consultado: [https://solucoes.receita.fazenda.gov.br/Servicos/certidaointernet/PJ/Emitir|https://solucoes.receita.fazenda.gov.br/Servicos/certidaointernet/PJ/Emitir|smart-link].

  Portaria [http://normas.receita.fazenda.gov.br/sijut2consulta/link.action?idAto=56753|http://normas.receita.fazenda.gov.br/sijut2consulta/link.action?idAto=56753|smart-link].

  [https://www.techtudo.com.br/dicas-e-tutoriais/2022/04/como-emitir-certidao-negativa-de-debitos-federais-de-uma-empresa.ghtml|https://www.techtudo.com.br/dicas-e-tutoriais/2022/04/como-emitir-certidao-negativa-de-debitos-federais-de-uma-empresa.ghtml|smart-link]


)
