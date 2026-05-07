# CONFIG_MAP

Este documento lista todos os Itens de Configuração (ICs) utilizados no projeto, incluindo bibliotecas, frameworks e outros componentes relevantes.  

Além disso, define a política de **nomenclatura de versões** adotada no projeto: utilizamos o **versionamento semântico (SemVer)** para todas as dependências e componentes desenvolvidos internamente.

## Versionamento Semântico (SemVer)

O **SemVer** segue o formato:

- **MAJOR**: Incrementado quando há mudanças incompatíveis na API.  
- **MINOR**: Incrementado quando são adicionadas funcionalidades de forma compatível.  
- **PATCH**: Incrementado quando são feitas correções de bugs compatíveis com a API.  

Exemplo: `2.3.1` indica a versão 2 (maior), 3 (menor) e 1 (patch).

## Política de Nomenclatura de Versões

1. Todas as bibliotecas externas devem manter a versão oficial da fonte original.  
2. Versões pré-lançamento podem utilizar sufixos como `-alpha`, `-beta` ou `-rc` (release candidate).

## Itens de Configuração (ICs)

Abaixo estão listados os principais arquivos e recursos do projeto que são considerados Itens de Configuração (ICs). Todos seguem a versão inicial **v1.0.0**.  

Observação: Outros arquivos dentro da pasta `src` também são ICs e devem ser incluídos na manutenção de versões conforme alterações.

| Nome do IC                                                                 | Tipo                                               | Versão   |
|----------------------------------------------------------------------------|---------------------------------------------------|----------|
| src/main/java/com/sistema_escolar/controllers/AuthenticationController.java | Controller que expõe endpoints de autenticação do sistema | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/DisciplinaController.java     | Controller que gerencia endpoints de disciplinas | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/EstatisticasController.java   | Controller que fornece estatísticas do sistema   | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/NotaController.java           | Controller que gerencia notas de alunos          | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/ProvaController.java          | Controller que gerencia provas                    | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/QuestaoController.java        | Controller que gerencia questões de provas       | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/RespostaProvaController.java  | Controller que gerencia respostas de provas      | v1.0.0 |
| src/main/java/com/sistema_escolar/controllers/TurmaController.java          | Controller que gerencia turmas                    | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/AddTurmaRequestDTO.java      | DTO de requisição para adicionar turmas          | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/CodeRequestDTO.java          | DTO de requisição genérico                        | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/CriarDisciplinaRequestDTO.java | DTO para criação de disciplina                   | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/CriarTurmaRequestDTO.java    | DTO para criação de turma                         | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/LoginRequestDTO.java         | DTO de requisição de login                        | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/MudarSenhaEmailRequestDTO.java | DTO para alteração de senha via email           | v1.0.0 |
| src/main/java/com/sistema_escolar/dtos/request/MudarSenhaRequestDTO.java    | DTO para alteração de senha                       | v1.0.0 |
| src/main/java/com/sistema_escolar/entities/Disciplina.java                  | Entidade representando disciplina                | v1.0.0 |
| src/main/java/com/sistema_escolar/exceptions/EntityNotFoundException.java   | Exceção lançada quando entidade não é encontrada | v1.0.0 |
| src/main/java/com/sistema_escolar/infra/security/SecurityConfigurations.java | Configurações de segurança do sistema            | v1.0.0 |
| src/main/java/com/sistema_escolar/repositories/DisciplinaRepository.java    | Repositório para manipulação de dados de disciplina | v1.0.0 |
| src/main/java/com/sistema_escolar/services/DisciplinaService.java           | Serviço que implementa regras de negócio de disciplinas | v1.0.0 |
| src/main/resources/application.properties                                  | Arquivo de configuração do Spring Boot          | v1.0.0 |
| docker-compose.yml                                                          | Configuração de containers  do sistema                     | v1.0.0 |
| pom.xml                                                                     | Arquivo de build Maven           com as dependências utilizadas pelo sistema                 | v1.0.0 |
| sonarqube/docker-compose.yml                                                | Configuração do SonarQube via container            | v1.0.0 |
| config.env                                                                  | Variáveis de ambiente                             | v1.0.0 |
| README.md                                                                   | Documentação do projeto                           | v1.0.0 |
| LICENSE                                                                     | Licença do projeto                                | v1.0.0 |
| .gitignore                                                                  | Arquivo de exclusão do Git                        | v1.0.0 |
