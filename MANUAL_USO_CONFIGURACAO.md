# Manual de Uso e Configuração - Projeto SAAM-CND

## 1. Visão Geral do Projeto SAAM-CND

O projeto SAAM-CND (Sistema de Acompanhamento e Monitoramento de Certidões Negativas de Débitos) tem como objetivo gerenciar e automatizar o processo de consulta e verificação de CNDs para clientes, integrando-se (futuramente) com portais da Receita Federal e Secretarias da Fazenda (SEFAZ).

**Arquitetura:**
*   **Backend:** Java com Spring Boot, expondo uma API RESTful.
*   **Frontend:** React, consumindo a API do backend.
*   **Banco de Dados:** PostgreSQL.
*   **Containerização:** Docker e Docker Compose para facilitar a execução e o deploy.

## 2. Pré-requisitos

*   Docker: [https://www.docker.com/get-started](https://www.docker.com/get-started)
*   Docker Compose (geralmente incluído com o Docker Desktop).

## 3. Como Executar o Projeto

1.  **Clone o repositório** para a sua máquina local.
2.  Navegue até o diretório raiz do projeto (`saam-cnd`).
3.  **Construa as imagens Docker:**
    ```bash
    docker-compose build
    ```
4.  **Inicie os containers:**
    ```bash
    docker-compose up -d
    ```
    O `-d` executa os containers em modo detached (background).

5.  **Acessando os Serviços:**
    *   **Frontend (Aplicação Web):** `http://localhost:3000`
    *   **Backend API (diretamente, se necessário):** `http://localhost:8080` (ex: `http://localhost:8080/api/clientes`)
    *   **Swagger UI (Documentação da API):** `http://localhost:8080/swagger-ui.html`
    *   **Banco de Dados PostgreSQL:** Acessível na porta `5432` (configurada no `docker-compose.yml` e `application.properties`).

Para parar os containers:
```bash
docker-compose down
```

## 4. Variáveis de Ambiente e Configurações Importantes

As principais configurações do backend estão no arquivo `saam-cnd/backend/src/main/resources/application.properties`.

*   `server.port=8080`: Porta em que o servidor backend Spring Boot executa dentro do container.
*   **Banco de Dados:**
    *   `spring.datasource.url=jdbc:postgresql://database:5432/saam_cnd_db`: URL de conexão JDBC. `database` é o nome do serviço do PostgreSQL no `docker-compose.yml`.
    *   `spring.datasource.username=saamuser`: Usuário do banco.
    *   `spring.datasource.password=saampass`: Senha do banco.
*   **Tarefa Agendada (Extração de PDF):**
    *   `cnd.resultado.scheduled.cron=0 */15 * * * *`: Define a frequência da tarefa de extração de dados de PDF (a cada 15 minutos por padrão). Você pode alterar essa expressão cron para testes (ex: `0 * * * * *` para a cada minuto).
*   **Validação de Cliente SAAM-CR:**
    *   `saam.cr.validation.url=http://saamauditoria-2.com.br:8085/api/empresa/getAttributeById/GLSAAM?attribute=situacao`: URL base do serviço externo para validar clientes.
    *   `saam.cr.validation.mock=true`:
        *   Se `true`, a chamada real ao serviço SAAM-CR é desabilitada e um mock interno é usado.
        *   **Comportamento do Mock:**
            *   Header `X-ID-CLIENTE` = "1": Cliente é considerado **autorizado**.
            *   Header `X-ID-CLIENTE` = "2": Cliente é considerado **não autorizado** (simula situação diferente de "1").
            *   Header `X-ID-CLIENTE` = "3": Simula um **erro de comunicação** com o serviço SAAM-CR.
            *   Outros IDs de cliente: Considerados **autorizados** por padrão no mock.
        *   Se `false`, o sistema tentará fazer a chamada real à `saam.cr.validation.url`.

## 5. Funcionalidades do Sistema

### 5.1. Gerenciamento de Clientes

*   **Acesso:** Menu lateral "Clientes".
*   **Listar/Paginar:** A tela exibe uma lista paginada de clientes cadastrados.
*   **Cadastrar Novo Cliente:**
    *   Clique em "Novo Cliente".
    *   Preencha os campos: Nome, CNPJ, Periodicidade (dias para consulta), Status do Cliente (ex: ATIVO), ID da Empresa (`fkEmpresa`).
    *   **Nota sobre `fkEmpresa`:** Este é o ID da tabela `cnd_empresa`. No sistema atual, se uma `fkEmpresa` é informada e não existe, o backend (via `CndEmpresaService`) tenta buscá-la de um "SAAM principal" (atualmente mockado, criando uma empresa fictícia se não encontrar).
    *   Selecione se o cliente terá consultas Nacionais, Municipais, Estaduais.
    *   Clique em "Salvar Cliente".
*   **Editar Cliente:** Clique no ícone de lápis na linha do cliente. Modifique os dados e salve.
*   **Excluir Cliente:** Clique no ícone de lixeira. Uma confirmação será solicitada. Um cliente não pode ser excluído se houver resultados de CND vinculados a ele.
*   **Atualizar Lista:** Clique no ícone de recarregar.

### 5.2. Monitoramento de Certidões Negativas de Débitos (CNDs)

*   **Acesso:** Menu lateral "CND Dashboard".
*   **Listar/Paginar:** Exibe resultados de CNDs processadas, com paginação.
*   **Filtros:** Expanda a seção "Filtros de Pesquisa" para filtrar por CNPJ do cliente, situação da certidão, status do processamento. Clique em "Aplicar Filtros".
*   **Ações na Linha de um Resultado de CND:**
    *   **Ver Detalhes (Ícone de olho):** Navega para uma tela com todos os detalhes do resultado da CND e do cliente associado.
    *   **Editar CND (Ícone de lápis):** Permite editar manualmente os campos de um resultado de CND (útil para correções ou inserções manuais).
    *   **Baixar PDF (Ícone de nuvem):** Se um arquivo PDF estiver associado ao resultado, permite o download. O nome do arquivo seguirá o padrão `CND_{CNPJ}_{ORGAO}_{DATA_EMISSAO}.pdf`. (Ex: `CND_12345678000199_RFB_2024-01-15.pdf`).
    *   **Consultar Federal (Ícone de banco):** (Simulado) Dispara uma consulta simulada à Receita Federal para o cliente vinculado a este resultado. Um novo `CndResultado` será criado com `tipoCertidao="Federal"`, `orgaoEmissor="Receita Federal do Brasil"`, dados mockados e `statusProcessamento="CONSULTA_REALIZADA"`.
    *   **Sincronizar MG (Ícone de sync):** (Simulado) Dispara uma consulta simulada à SEFAZ-MG. Um novo `CndResultado` será criado com `tipoCertidao="Estadual"`, `orgaoEmissor="SEFAZ-MG"`, dados mockados e `statusProcessamento="CONSULTA_REALIZADA"`.
    *   **Excluir Resultado (Ícone de lixeira):** Remove o registro do resultado da CND.
*   **Cadastrar Nova CND (Manual):**
    *   Clique no botão "Nova CND (Manual)".
    *   Selecione um cliente associado (se não for edição).
    *   Preencha os detalhes da certidão: Tipo (Federal, Estadual, Municipal), Órgão Emissor, Situação, Datas, Código de Controle, Observações.
    *   Clique em "Cadastrar Nova CND". Isso cria um `CndResultado` com os dados fornecidos.
*   **Extração Automática de Dados do PDF:**
    *   Esta é uma tarefa agendada no backend (configurada por `cnd.resultado.scheduled.cron`).
    *   Ela busca por `CndResultado` com `statusProcessamento="CONSULTA_REALIZADA"` (gerados pelas consultas simuladas) e onde a `situacao` ainda não foi preenchida.
    *   A tarefa tenta ler o conteúdo do PDF (que é mockado pelos serviços de consulta) e extrair campos como Situação, Data de Emissão, Data de Validade e Código de Controle.
    *   Após a tentativa, o `statusProcessamento` do `CndResultado` é atualizado para `EXTRACAO_CONCLUIDA` ou um status de erro (ex: `ERRO_EXTRACAO_DADOS_NAO_ENCONTRADOS`).
    *   Os resultados atualizados serão refletidos no dashboard.

### 5.3. Validação de Cliente (Integração SAAM-CR)

*   Todas as chamadas à API do backend (exceto Swagger) passam por um interceptor que verifica o header `X-ID-CLIENTE`.
*   O valor deste header é usado para consultar o serviço SAAM-CR (real ou mockado, conforme `saam.cr.validation.mock`).
*   **Teste com Mock (`saam.cr.validation.mock=true`):**
    *   No frontend, o `apiService.js` envia `X-ID-CLIENTE: 1` por padrão. Este cliente será **autorizado**.
    *   Para testar "não autorizado", você pode alterar temporariamente o header no `apiService.js` para `X-ID-CLIENTE: 2`. O frontend deverá lidar com um erro 403.
    *   Para testar "erro de serviço", use `X-ID-CLIENTE: 3`. O frontend deverá lidar com um erro 503.
*   Se a validação falhar (não autorizado ou erro no serviço de validação), a API retornará o status HTTP apropriado (403 Forbidden, 503 Service Unavailable, ou 400 Bad Request se o header estiver ausente).

## 6. Desenvolvimento e Testes

*   **Backend:**
    *   O código-fonte está em `saam-cnd/backend`.
    *   Testes unitários estão em `saam-cnd/backend/src/test/java`.
    *   Para executar os testes do backend:
        ```bash
        cd saam-cnd/backend
        mvn test
        ```
    *   As consultas externas à Receita Federal e SEFAZ são **simuladas** nos respectivos services (`CndFederalService.java`, `SincronizacaoMgService.java`). Eles geram dados mockados para `CndResultado`.
    *   A extração de PDF (`PdfExtractionScheduledTask.java`) também opera sobre esses dados mockados, usando regex simples para simular a extração.

*   **Frontend:**
    *   O código-fonte está em `saam-cnd/frontend`.
    *   Para rodar o frontend em modo de desenvolvimento local (fora do Docker):
        ```bash
        cd saam-cnd/frontend
        npm install
        npm start
        ```
        (Requer que o backend esteja rodando, e o proxy no `package.json` esteja configurado corretamente para o backend).

## 7. Pontos Futuros / Limitações da Simulação

*   **Consultas Externas Reais:** As interações com os portais da Receita Federal e SEFAZ atualmente são simuladas. Para um ambiente de produção, seria necessário implementar a lógica real de web scraping (ex: com Selenium, Puppeteer, Playwright) ou integração via API, se disponível.
*   **Extração de PDF Real:** A extração de dados de PDFs reais é complexa devido à variedade de layouts e formatos. A lógica atual com regex simples em `PdfExtractionScheduledTask.java` é uma simulação e precisaria ser substituída por técnicas mais robustas de OCR e parsing de PDF para produção.
*   **Gerenciamento de Empresas (`fkEmpresa`):** A obtenção de dados da "empresa principal" no `CndEmpresaService` também é simulada. Uma integração real seria necessária.
*   **Segurança:** A autenticação/autorização atual é baseada no `X-ID-CLIENTE` e na validação SAAM-CR. Para produção, um esquema de autenticação mais robusto (ex: OAuth2/JWT) para proteger a API seria recomendado.

Este manual deve ajudar a dar os primeiros passos com o projeto SAAM-CND.
```
