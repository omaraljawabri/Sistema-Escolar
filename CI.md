# 🚀 CI/CD do Sistema-Escolar

Este documento explica os workflows do GitHub Actions do projeto: o que cada um faz, quando é executado e o que precisa estar configurado para que funcionem.

## 🌳 Fluxo de branches e ambientes

| Branch / Tag | Ambiente | O que acontece |
|---|---|---|
| Pull Request para `homol` ou `main` | - | Apenas a integração contínua (análises, testes e build da imagem, sem publicação) |
| `homol` | Homologação | Integração contínua + deploy em homologação + smoke test + DAST |
| `main` | Produção | Integração contínua + deploy em produção (após aprovação manual) |
| Tag `vX.Y.Z` | - | Release: versiona a imagem, gera SBOMs e cria o GitHub Release |

    feature/* ──PR──► homol ──push──► CI ──► imagem ──► deploy homolog ──► smoke test + OWASP ZAP
                        │
                        └──PR──► main ──push──► CI ──► imagem ──► [aprovação] ──► deploy produção
                                                  │
    git tag vX.Y.Z (em um commit da main) ──► imagem vX.Y.Z + SBOM + GitHub Release

## ⚙️ Workflow de CI/CD (`.github/workflows/ci-cd.yml`)

Executado em pull requests e pushes para `homol` e `main`, e também manualmente pela aba *Actions* (`workflow_dispatch`).

### 🔍 Verificação estática
- `lint`
  - **Checkstyle**: primeiro portão do pipeline, executado em segundos em todo PR e push. Verifica regras de qualidade do código definidas no `checkstyle.xml`, como imports não utilizados ou com `*`, blocos vazios, `equals` sem `hashCode`, `switch` sem `default`, estruturas sem chaves e nomes fora do padrão Java. Regras de formatação e de Javadoc não são aplicadas. Se o lint falhar, os jobs de CodeQL e de testes nem são executados (*fail-fast*)
- `analise-estatica`
  - **Gitleaks**: procura segredos (senhas, tokens, chaves) em todo o histórico do repositório. Ocorrências antigas já analisadas ficam registradas no arquivo `.gitleaksignore`
  - **Dependency Review** (apenas em PRs): bloqueia o PR caso ele adicione uma dependência com vulnerabilidade de severidade alta ou crítica
- `codeql`
  - **CodeQL**: análise estática de segurança (SAST) do próprio GitHub. Os alertas aparecem na aba *Security*
- `sonarqube`
  - **SonarQube Cloud**: analisa bugs, vulnerabilidades, code smells, duplicação e cobertura de testes. O pipeline aguarda o resultado do **Quality Gate** e falha caso ele seja reprovado
  - Executado apenas em pull requests para a `main` e em pushes na `main`, pois o plano gratuito do SonarQube Cloud analisa somente a branch principal e os pull requests direcionados a ela. Nos demais casos o job é ignorado e o pipeline segue normalmente. Assim, o Quality Gate funciona como barreira para a promoção de homologação para produção (PR `homol` → `main`)

### 🧪 Verificação dinâmica
- `testes-unitarios`: executa `./mvnw test` e publica o relatório de cobertura do JaCoCo
- `testes-integracao`: executa `./mvnw verify -DskipUTs`, subindo um PostgreSQL e um servidor de e-mails falso com **Testcontainers**, e publica o relatório de cobertura do JaCoCo
- O job `sonarqube` combina os dois relatórios de cobertura

### 📦 Build da imagem
- `build-imagem`
  - Gera a imagem Docker a partir do `Dockerfile`, gravando o commit na imagem (exibido em `/actuator/info`)
  - **Trivy**: procura vulnerabilidades conhecidas (CVEs) na imagem. O pipeline falha em vulnerabilidades críticas ou altas que já possuem correção. O resultado aparece na aba *Security*
  - Em pushes para `homol` e `main`, publica a imagem no GitHub Container Registry como `ghcr.io/omaraljawabri/sistema-escolar:sha-<commit>`

### 🧩 Homologação (branch `homol`)
- `deploy-homolog`
  - Solicita ao Render o deploy da imagem do commit (`.github/scripts/deploy-render.sh`)
  - Aguarda até que `/actuator/info` responda com o commit implantado e que `/actuator/health/readiness` esteja `UP`
  - **Smoke test** (`.github/scripts/smoke-test.sh`): cadastra um estudante, faz login e acessa um endpoint protegido, com e sem token
- `dast-homolog`
  - **OWASP ZAP**: executa ataques simulados contra a API em homologação a partir da especificação OpenAPI (`/v3/api-docs`). As regras de `.zap/rules.tsv` definem quais vulnerabilidades reprovam o pipeline (injeção de SQL, XSS, injeção de comandos, etc.). O relatório completo fica disponível como artefato da execução

### 🏭 Produção (branch `main`)
- `deploy-producao`
  - Aguarda a **aprovação manual** de um revisor, configurada no environment `production`
  - Solicita ao Render o deploy da imagem do commit e aguarda a nova versão responder com o health check `UP`
  - Não executa o smoke test, para não criar dados de teste no banco de produção

## 🏷️ Workflow de Release (`.github/workflows/release.yml`)

Executado quando uma tag no formato `vX.Y.Z` é enviada ao repositório.

- `validar`
  - Verifica se a tag aponta para um commit da branch `main`
  - Verifica se a versão da tag é igual à versão do `pom.xml` (ex.: a tag `v1.2.0` exige `<version>1.2.0</version>`), seguindo o versionamento semântico definido no `CONFIG_MAP.md`
- `publicar`
  - Verifica se a imagem `sha-<commit>` existe, ou seja, se o commit passou pelo pipeline da `main`
  - Adiciona as tags `vX.Y.Z` e `latest` à imagem existente, **sem gerar uma nova build**
  - Gera dois SBOMs (lista de todos os componentes do software):
    - `sbom-aplicacao-vX.Y.Z.cdx.json` (CycloneDX): dependências Maven da aplicação, extraído da própria imagem
    - `sbom-imagem-vX.Y.Z.spdx.json` (SPDX): todos os pacotes da imagem Docker, gerado pelo Syft
  - Cria o **GitHub Release** com as notas geradas a partir dos PRs e os SBOMs anexados

### Como criar uma release
    1. Atualize a versão no pom.xml (ex.: 1.1.0) e leve a alteração até a main pelo fluxo normal (homol → main).
    2. Aguarde o pipeline da main terminar.
    3. Na main atualizada, execute os comandos:
       git tag v1.1.0
       git push origin v1.1.0
    4. Acompanhe o workflow Release na aba Actions e confira o release criado na página de Releases.

## 📌 Versões das actions
As actions de terceiros (Docker, Trivy, OWASP ZAP, Gitleaks, Syft e a de criação de releases) são referenciadas pelo **SHA completo do commit**, com a versão correspondente ao lado (ex.: `docker/login-action@dbcb8138... # v4.6.0`). Diferente de uma tag, que pode ser movida para apontar para outro código, o SHA garante que o pipeline sempre executa exatamente o código revisado, protegendo contra ataques de cadeia de suprimentos. O SonarQube Cloud reprova o Quality Gate caso alguma action de terceiros seja referenciada apenas pela tag. As actions oficiais do GitHub (`actions/*` e `github/*`) são referenciadas pela versão principal (ex.: `@v7`).

## 🤖 Dependabot (`.github/dependabot.yml`)

Semanalmente, o Dependabot abre PRs para a branch `homol` com atualizações de:
- Dependências Maven (atualizações minor e patch agrupadas em um único PR)
- Actions utilizadas nos workflows
- Imagens base do `Dockerfile`

Como os PRs apontam para `homol`, toda atualização passa pela homologação antes de chegar à produção.

Atualizações de versão **major** das dependências Maven e das imagens Docker são ignoradas, pois costumam trazer mudanças incompatíveis e exigem uma migração planejada. Por exemplo: Spring Boot 3 → 4, springdoc 2 → 3 (que depende do Spring Boot 4) e troca da versão do Java da imagem base (21 → 24, que não é uma versão LTS). Essas atualizações devem ser feitas manualmente, em um PR próprio.

`Obs: o Dependabot lê o arquivo de configuração da branch padrão (main), então alterações no dependabot.yml só passam a valer depois de chegarem à main`

## 🔐 Configuração necessária

### Segurança do repositório (Settings → Advanced Security)
- *Dependency graph*: habilitado (necessário para o Dependency Review)
- *Dependabot alerts*: habilitado

### Secrets e variáveis do repositório
- `SONAR_TOKEN` (secret do repositório e secret do Dependabot): token do SonarQube Cloud

### Environments (Settings → Environments)
| Environment | Secret | Variável | Proteção |
|---|---|---|---|
| `homolog` | `RENDER_DEPLOY_HOOK_URL` (deploy hook do serviço de homologação) | `APP_URL` (URL pública de homologação) | - |
| `production` | `RENDER_DEPLOY_HOOK_URL` (deploy hook do serviço de produção) | `APP_URL` (URL pública de produção) | Revisor obrigatório |

### Render
Cada ambiente é um *Web Service* do Render criado a partir de uma imagem existente (`ghcr.io/omaraljawabri/sistema-escolar`), com:
- *Auto-Deploy* desabilitado (quem dispara o deploy é o pipeline)
- *Health Check Path*: `/actuator/health/readiness`
- Variáveis de ambiente próprias, principalmente `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `AUTH_TOKEN_SECRET` (diferente em cada ambiente), `APP_BASE_URL`, `CORS_ALLOWED_ORIGINS` e `MAIL_ENABLED=false`

`Obs: no plano gratuito, o Render desliga o serviço após 15 minutos sem acessos e leva cerca de 1 minuto para religá-lo. Por isso, o script de deploy aguarda até 15 minutos pela nova versão`

## 🛠️ Executando as verificações localmente
    Lint: ./mvnw checkstyle:check
    Testes unitários: ./mvnw test
    Testes de integração: ./mvnw verify -DskipUTs
    Segredos no histórico: docker run --rm -v "$PWD:/repo" ghcr.io/gitleaks/gitleaks:latest git /repo
    Validação dos workflows: docker run --rm -v "$PWD:/repo" -w /repo rhysd/actionlint:latest
