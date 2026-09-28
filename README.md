# 🏫 Sistema-Escolar
 Projeto de uma API de um Sistema Escolar utilizando roles (ADMIN, PROFESSOR e ESTUDANTE) e autenticação com token JWT

## Versão do Projeto

 v1.1.0

## 💻 Tecnologias utilizadas
- Java 21
- Spring Boot 3.5
- Docker e Docker Compose
- Maven (com Maven Wrapper)
- PostgreSQL
- Flyway
- SonarQube
- JUnit
- Testcontainers
- JaCoCo
- Mailpit
- GitHub Actions
- Render

## 📋 Requisitos mínimos
    Possuir o Docker instalado e funcionando em sua máquina
    (Opcional) Possuir o Java 21 instalado caso queira rodar os testes em sua máquina (não é necessário instalar o Maven, o ./mvnw faz o download automaticamente)

## ⚙️ Aplicação - Como rodar
    1. Faça o clone desse repositório ou baixe a versão zip e descompacte.
    2. Abra o terminal no repositório.
    3. Crie o arquivo .env a partir do exemplo, executando o comando: cp .env.example .env
    4. Abra o arquivo .env e troque o valor de AUTH_TOKEN_SECRET por um segredo seu (veja a seção de configuração mais abaixo).
    5. Execute o comando: docker compose up --build
    6. A aplicação está no ar! http://localhost:8080
    7. Os e-mails enviados pela aplicação (validação de cadastro, redefinição de senha, notas, etc.) podem ser vistos no Mailpit: http://localhost:8025
    Obs: para parar a aplicação, execute o comando docker compose down (adicione -v caso queira apagar também os dados do banco)
### 📄 Documentação
    Caso queira ter acesso a uma documentação mais detalhada, após rodar a aplicação, acesse o link: http://localhost:8080/swagger-ui/index.html
### ❤️ Health checks
    A aplicação expõe os seguintes endpoints para verificar se ela está funcionando corretamente:
    - http://localhost:8080/actuator/health/liveness: indica se a aplicação está no ar
    - http://localhost:8080/actuator/health/readiness: indica se a aplicação está pronta para receber requisições (inclui a conexão com o banco de dados)
    - http://localhost:8080/actuator/info: exibe a versão e o commit da build que está rodando

## ⚙️ Configuração do arquivo .env
Toda a configuração da aplicação é feita por variáveis de ambiente, assim a mesma imagem Docker pode ser utilizada em qualquer ambiente (local, homologação e produção), mudando apenas os valores das variáveis. Ao rodar com o Docker Compose, os valores são lidos do arquivo `.env` na raiz do projeto, que deve seguir o exemplo abaixo (o mesmo do arquivo `.env.example`).
```bash
DB_USERNAME=postgres
DB_PASSWORD=postgres

AUTH_TOKEN_SECRET=<seu_segredo>
AUTH_TOKEN_EXPIRATION_HOURS=2

APP_BASE_URL=http://localhost:8080
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:3000

MAIL_ENABLED=true
MAIL_HOST=mailpit
MAIL_PORT=1025
MAIL_USERNAME=no-reply@sistema-escolar.local
MAIL_PASSWORD=
MAIL_SMTP_AUTH=false
MAIL_SMTP_STARTTLS=false
```
`Obs: o arquivo .env contém informações sensíveis e não deve ser enviado para o repositório (ele já está no .gitignore)`

### 🔐 Variáveis de ambiente
- `PORT`: porta HTTP da aplicação (padrão: 8080)
- `DB_URL`: URL JDBC do PostgreSQL (padrão: jdbc:postgresql://localhost:5432/sistema-escolar-db). No Docker Compose ela já é definida automaticamente
- `DB_USERNAME`: usuário do banco de dados (padrão: postgres)
- `DB_PASSWORD`: senha do banco de dados (padrão: postgres)
- `AUTH_TOKEN_SECRET`: segredo utilizado para assinar os tokens JWT (obrigatória, sem valor padrão)
- `AUTH_TOKEN_EXPIRATION_HOURS`: tempo de validade do token JWT, em horas (padrão: 2)
- `APP_BASE_URL`: URL pública da API, utilizada nos links enviados por e-mail (padrão: http://localhost:8080)
- `CORS_ALLOWED_ORIGINS`: origens liberadas no CORS, separadas por vírgula (padrão: http://localhost:4200,http://localhost:3000)
- `MAIL_ENABLED`: habilita ou desabilita o envio de e-mails (padrão: true)
- `MAIL_HOST`: endereço do servidor SMTP (padrão: localhost). No Docker Compose, utilize mailpit para usar o servidor de e-mails falso que sobe junto com a aplicação
- `MAIL_PORT`: porta do servidor SMTP (padrão: 1025)
- `MAIL_USERNAME`: usuário do servidor SMTP, também utilizado como remetente dos e-mails (padrão: no-reply@sistema-escolar.local)
- `MAIL_PASSWORD`: senha do servidor SMTP (padrão: vazio)
- `MAIL_SMTP_AUTH`: habilita a autenticação no servidor SMTP (padrão: false)
- `MAIL_SMTP_STARTTLS`: habilita o STARTTLS no servidor SMTP (padrão: false)
- `APP_COMMIT`: commit exibido em /actuator/info, definido no momento do build da imagem Docker (padrão: local)

`Obs: para gerar um valor seguro para o AUTH_TOKEN_SECRET, execute o comando: openssl rand -base64 32`

### 📧 Envio de e-mails
    Por padrão, o Docker Compose sobe junto com a aplicação o Mailpit, um servidor de e-mails falso. Nenhum e-mail sai de verdade, todos ficam disponíveis em http://localhost:8025
    Caso queira enviar e-mails de verdade pelo Gmail, altere as seguintes variáveis no arquivo .env:
    1. MAIL_HOST=smtp.gmail.com
    2. MAIL_PORT=587
    3. MAIL_USERNAME=<seu_email>
    4. MAIL_PASSWORD=<sua_senha_de_app>
    5. MAIL_SMTP_AUTH=true
    6. MAIL_SMTP_STARTTLS=true
`Obs: caso não saiba quais dados colocar nos campos acima, visite o site https://support.google.com/accounts/answer/185833 para mais informações`

### 🚫 Rodando sem envio de e-mails
    Caso não queira configurar nenhum servidor de e-mails (por exemplo, em um ambiente de homologação ou de demonstração), defina a variável MAIL_ENABLED=false. Dessa forma:
    - Nenhum e-mail é enviado pela aplicação
    - As contas de usuários já são criadas como verificadas, permitindo fazer login logo após o cadastro
`Obs: com o envio de e-mails desabilitado, a redefinição de senha não funciona, pois o código de verificação é enviado por e-mail`

## 🗄️ Banco de dados
    O schema do banco de dados é versionado com o Flyway e as migrations são aplicadas automaticamente quando a aplicação sobe.
    As migrations ficam na pasta src/main/resources/db/migration
    Qualquer alteração nas entidades deve vir acompanhada de uma nova migration, seguindo o padrão V<número>__<descricao>.sql (ex.: V2__adicionar_coluna_telefone.sql)
`Obs: caso você já possua um volume do PostgreSQL criado por uma versão anterior do projeto (sem Flyway), execute o comando docker compose down -v antes de subir a aplicação`

## 📝 Testes unitários e de integração

A aplicação contém testes unitários e de integração, com uma cobertura de 85,8% de acordo com o JaCoCo. Os testes de integração utilizam o Testcontainers para subir automaticamente um PostgreSQL e um servidor de e-mails falso (Mailpit), então não é necessário configurar nenhuma credencial, basta ter o Docker rodando. Caso queira rodá-los, siga o passo a passo abaixo:

### 🧪 Testes unitários
    1. Abra o terminal no repositório.
    2. Execute o comando: ./mvnw test
### 🧪 Testes de integração
    1. Certifique-se de que o Docker está rodando.
    2. Abra o terminal no repositório.
    3. Execute o comando: ./mvnw verify -DskipUTs
### 🧪 Testes unitários e de integração
    1. Certifique-se de que o Docker está rodando.
    2. Abra o terminal no repositório.
    3. Execute o comando: ./mvnw verify
    4. O relatório de cobertura do JaCoCo é gerado em target/site/jacoco/index.html
`Obs: no Windows, utilize mvnw.cmd no lugar de ./mvnw`

## 🧹 Lint (Checkstyle)
O código é verificado pelo Checkstyle, com as regras definidas no arquivo checkstyle.xml. Essa verificação também é executada automaticamente na pipeline de CI/CD, então rode-a antes de abrir um PR:

    1. Abra o terminal no repositório.
    2. Execute o comando: ./mvnw checkstyle:check

## 🚀 CI/CD
A aplicação possui uma pipeline de CI/CD no GitHub Actions com lint (Checkstyle), análise estática (Gitleaks, CodeQL e SonarQube Cloud), testes automatizados, scan de vulnerabilidades da imagem Docker (Trivy), deploy em homologação (branch homol) e em produção (branch main) no Render, testes dinâmicos em homologação (smoke test e OWASP ZAP) e geração de releases com SBOM a partir de tags.

Caso queira entender em detalhes como os workflows funcionam e o que é necessário para configurá-los, acesse o arquivo [CI.md](CI.md).

`Obs: para criar uma nova release, siga o passo a passo da seção "Como criar uma release" do CI.md`
