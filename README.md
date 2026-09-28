# 🏫 Sistema-Escolar
 Projeto de uma API de um Sistema Escolar utilizando roles (ADMIN, PROFESSOR e ESTUDANTE) e autenticação com token JWT

## Versão do Projeto

 v1.0.0

## 💻 Tecnologias utilizadas
- Java 21
- Spring Boot 3.5
- PostgreSQL + Flyway (migrations)
- Docker / Docker Compose
- Maven (com Maven Wrapper)
- JUnit, Mockito e Testcontainers
- JaCoCo
- SonarQube

## 📋 Requisitos mínimos
    Possuir o Docker instalado e funcionando em sua máquina
    (Opcional) Possuir o Java 21 instalado caso queira rodar os testes em sua máquina (o Maven é baixado automaticamente pelo ./mvnw)

## ⚙️ Aplicação - Como rodar
    1. Faça o clone desse repositório ou baixe a versão zip e descompacte.
    2. Copie o arquivo .env.example para .env e ajuste os valores (no mínimo o AUTH_TOKEN_SECRET).
    3. Abra o terminal no repositório.
    4. Execute o comando: docker compose up --build
    5. A aplicação está no ar! http://localhost:8080
    6. Os e-mails enviados pela aplicação (cadastro, redefinição de senha, etc.) podem ser vistos no Mailpit: http://localhost:8025

### 📄 Documentação
    Caso queira ter acesso a uma documentação mais detalhada, após rodar a aplicação, acesse o link: http://localhost:8080/swagger-ui/index.html

### ❤️ Health checks
| Endpoint | Descrição |
|---|---|
| `/actuator/health/liveness` | A aplicação está viva |
| `/actuator/health/readiness` | A aplicação está pronta para receber requisições (inclui conexão com o banco) |
| `/actuator/info` | Versão e commit da build implantada |

## 🔐 Variáveis de ambiente
Toda a configuração da aplicação é feita por variáveis de ambiente, de forma que a mesma imagem Docker é utilizada em qualquer ambiente (local, homologação e produção). Veja o arquivo `.env.example`.

| Variável | Padrão | Descrição |
|---|---|---|
| `PORT` | `8080` | Porta HTTP da aplicação |
| `DB_URL` | `jdbc:postgresql://localhost:5432/sistema-escolar-db` | URL JDBC do PostgreSQL |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `AUTH_TOKEN_SECRET` | **obrigatória** | Segredo usado para assinar os tokens JWT |
| `AUTH_TOKEN_EXPIRATION_HOURS` | `2` | Validade do token JWT, em horas |
| `APP_BASE_URL` | `http://localhost:8080` | URL pública da API, usada nos links enviados por e-mail |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200,http://localhost:3000` | Origens liberadas no CORS (separadas por vírgula) |
| `MAIL_HOST` | `localhost` | Servidor SMTP |
| `MAIL_PORT` | `1025` | Porta do servidor SMTP |
| `MAIL_USERNAME` | `no-reply@sistema-escolar.local` | Usuário SMTP (também usado como remetente) |
| `MAIL_PASSWORD` | vazio | Senha SMTP |
| `MAIL_SMTP_AUTH` | `false` | Habilita autenticação SMTP |
| `MAIL_SMTP_STARTTLS` | `false` | Habilita STARTTLS |
| `APP_COMMIT` | `local` | Commit exibido em `/actuator/info` (definido no build da imagem) |

`Obs: para enviar e-mails de verdade pelo Gmail, use MAIL_HOST=smtp.gmail.com, MAIL_PORT=587, MAIL_SMTP_AUTH=true, MAIL_SMTP_STARTTLS=true e uma senha de app (https://support.google.com/accounts/answer/185833)`

## 🗄️ Banco de dados
O schema do banco é versionado com o Flyway. As migrations ficam em `src/main/resources/db/migration` e são aplicadas automaticamente quando a aplicação sobe. Qualquer alteração nas entidades deve vir acompanhada de uma nova migration (ex.: `V2__descricao.sql`).

`Obs: caso você já tenha um volume do PostgreSQL criado por versões anteriores do projeto (sem Flyway), remova-o com docker compose down -v antes de subir a aplicação.`

## 📝 Testes unitários e de integração

A aplicação contém testes unitários e de integração. Os testes de integração utilizam o **Testcontainers** para subir automaticamente um PostgreSQL e um servidor SMTP falso (Mailpit), por isso **é necessário ter o Docker rodando** para executá-los. Nenhuma credencial externa é necessária.

### 🧪 Testes unitários
    1. Abra o terminal no repositório.
    2. Execute o comando: ./mvnw test
### 🧪 Testes unitários e de integração
    1. Abra o terminal no repositório.
    2. Execute o comando: ./mvnw verify
    3. O relatório de cobertura do JaCoCo é gerado em target/site/jacoco/index.html
