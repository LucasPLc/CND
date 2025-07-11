# SAAM-CND (Sistema de Acompanhamento e Análise de Movimentações - Certidões Negativas de Débitos)

Este projeto consiste em um sistema Full-Stack para gerenciamento e consulta de Certidões Negativas de Débitos (CND).

## Tecnologias Utilizadas

- **Back-end:** Java com Spring Boot
- **Front-end:** React
- **Banco de Dados:** PostgreSQL
- **Containerização:** Docker e Docker Compose

## Estrutura do Projeto

O projeto está dividido em duas pastas principais:

- `backend/`: Contém a aplicação Spring Boot (API RESTful).
- `frontend/`: Contém a aplicação React (Interface do Usuário).

## Pré-requisitos

- Docker
- Docker Compose
- Java 11 (ou superior) para desenvolvimento local do backend (opcional se usar apenas Docker)
- Node.js e npm para desenvolvimento local do frontend (opcional se usar apenas Docker)
- Maven para gerenciamento de dependências do backend (opcional se usar apenas Docker)

## Como Executar com Docker

1.  **Clone o repositório:**
    ```bash
    git clone <url_do_repositorio>
    cd saam-cnd
    ```

2.  **Construa e execute os containers Docker:**
    ```bash
    docker-compose up --build
    ```

    Este comando irá:
    - Construir as imagens Docker para o back-end e front-end.
    - Iniciar os containers para a API, a UI e o banco de dados PostgreSQL.

3.  **Acesse as aplicações:**
    - **Front-end (UI):** [http://localhost:3000](http://localhost:3000)
    - **Back-end (API Swagger UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    - **Back-end (API Docs JSON):** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Desenvolvimento Local (Opcional)

Se preferir rodar as aplicações localmente sem Docker (ou para debug mais fácil):

### Back-end (Spring Boot)

1.  Navegue até a pasta `backend/`.
2.  Execute o servidor PostgreSQL (pode ser via Docker do `docker-compose.yml` ou uma instância local).
    ```bash
    # Se for usar o Postgres do docker-compose.yml, em outra aba do terminal:
    docker-compose up -d database
    ```
3.  Configure as propriedades do banco de dados em `src/main/resources/application.properties` se necessário (já está configurado para o Docker).
4.  Execute a aplicação Spring Boot:
    ```bash
    ./mvnw spring-boot:run
    # Ou, se tiver Maven instalado globalmente:
    # mvn spring-boot:run
    ```
    A API estará disponível em `http://localhost:8080`.

### Front-end (React)

1.  Navegue até a pasta `frontend/`.
2.  Instale as dependências:
    ```bash
    npm install
    ```
3.  Inicie o servidor de desenvolvimento React:
    ```bash
    npm start
    ```
    A UI estará disponível em `http://localhost:3000` e fará proxy das chamadas de API para `http://localhost:8080` (se o backend estiver rodando).

## Parando a Aplicação Docker

Para parar todos os containers iniciados com `docker-compose`:

```bash
docker-compose down
```

Para remover os volumes (cuidado, isso apagará os dados do banco de dados):

```bash
docker-compose down -v
```

## Próximos Passos (Conforme Plano de Execução)

Consultar o plano de execução para as próximas etapas de desenvolvimento das funcionalidades específicas.
O GEMINI.md (ou AGENTS.md) neste repositório contém as diretrizes detalhadas.
