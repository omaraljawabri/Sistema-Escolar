# 🏫 Sistema-Escolar
 Projeto de uma API de um Sistema Escolar utilizando roles (ADMIN, PROFESSOR e ESTUDANTE) e autenticação com token JWT

## Versão do Projeto

 v1.0.0

## 💻 Tecnologias utilizadas
- Java
- Spring
- Docker 
- Maven
- SonarQube
- JUnit
- JaCoCo
- PostgreSQL
- Jib para Docker image

## 📋 Requisitos mínimos
    Possuir o Docker instalado e funcionando em sua máquina
    (Opcional) Possuir maven instalado e funcionando caso queira rodar os testes em sua máquina

## ⚙️ Aplicação - Como rodar
    1. Faça o clone desse repositório ou baixe a versão zip e descompacte.
    2. Crie um arquivo .env e adicione as informações necessárias, como no exemplo mais abaixo.
    3. Abra o terminal no repositório.
    4. Execute o comando: docker-compose up.
    5. A aplicação está no ar! http://localhost:8080
### 📄 Documentação 
    Caso queira ter acesso a uma documentação mais detalhada, após rodar a aplicação, acesse o link: http://localhost:8080/swagger-ui/index.html

## ⚙️ Configuração do arquivo .env
Para ter acesso a funcionalidade de envio de e-mails que a aplicação proporciona, crie um arquivo `.env` na raiz do projeto seguindo o exemplo abaixo e substituindo os dados pelos seus próprios.
```bash
# Configurações do envio de e-mails
MAIL_USERNAME=<seu_usuario>
MAIL_PASSWORD=<sua_senha>
```
`Obs: caso não saiba quais dados colocar nos campos abaixo, visite o site https://support.google.com/accounts/answer/185833 para mais informações` 

## 📝 Testes unitários e de integração

A aplicação contém testes unitários e de integração, com uma cobertura de 80,6% de acordo com o SonarQube e JaCoCo, caso queira rodá-los, siga o passo a passo abaixo:

### Setup
    Antes de iniciar os testes, certifique-se de que suas credenciais para envio de e-mails estão sendo lidas a partir dos seguintes passos:
    1. Abra o terminal no repositório.
    2. Execute o comando: set MAIL_USERNAME=<seu_email>
    3. Execute o comando: set MAIL_PASSWORD=<sua_senha>
    Obs: O e-mail e senha passados são os mesmos que foram colocados no arquivo .env
### 🧪 Testes unitários
    1. Abra o terminal no repositório.
    2. Execute o comando: mvn test
### 🧪 Testes de integração
    1. Abra o terminal no repositório.
    2. Execute o comando mvn test -Pintegration-tests
### 🧪 Testes unitários e de integração
    1. Abra o terminal no repositório.
    2. Execute o comando mvn test -Pall-tests
