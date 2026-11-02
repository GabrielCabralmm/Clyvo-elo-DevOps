# CLYVO Elo — Painel da Clínica no Azure com CI/CD

Entrega da disciplina de **DevOps Tools & Cloud Computing (Sprint 4)** do Challenge FIAP 2026, proposto pela **CLYVO VET**.

O painel web do CLYVO Elo, feito em **Spring Boot**, é publicado no **Azure Web App** e usa o **Azure Database for PostgreSQL** como banco em nuvem. Uma pipeline de **CI** compila, testa e publica o artefato. Uma pipeline de **CD** faz o deploy automático a cada artefato novo. As duas rodam no **Azure DevOps**.

| | |
|---|---|
| Aplicação | https://clyvo-elo-rm563230.azurewebsites.net |
| Azure DevOps | [a preencher] |
| Vídeo | [a preencher] |

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Gabriel Cabral Mendes Mariano | RM563230 | 2TDSPO |
| João Victor Santana dos Santos | RM566063 | 2TDSPX |

---

## 1. Descrição da solução

O desafio da CLYVO VET é manter a **jornada contínua de saúde do pet**. O cuidado não pode acabar quando o tutor sai da consulta. Hoje, o que mais se perde está entre uma visita e outra: o retorno do pós-operatório, a segunda dose da vacina, a orientação do veterinário e os sinais de alerta que o tutor percebe em casa.

O **Painel da Clínica** liga a clínica e o tutor depois da consulta. Ele tem três perfis:

| Perfil | O que faz |
|---|---|
| **Administrador** | Cadastra tutores, pets e a equipe, e acompanha os indicadores da clínica num dashboard |
| **Veterinário** | Agenda e atende os eventos clínicos (vacina, check-up, pós-operatório, medicação, exame), reagenda e acompanha os alertas até a resolução |
| **Tutor** | Vê a agenda e o histórico dos próprios pets, lê as orientações do veterinário e relata sintomas |

As tabelas centrais da solução são **tutor → pet → evento clínico / alerta**. O CRUD completo demonstrado na entrega é feito sobre **tutores e pets**, duas tabelas relacionadas por chave estrangeira.

### Benefícios para o negócio

- **Menos abandono de tratamento:** retornos e doses ficam agendados e aparecem como *Atrasado* quando vencem, para a clínica agir antes de perder o paciente.
- **Mais retorno à clínica:** cada cuidado agendado é uma nova visita, o que aumenta a recorrência e o faturamento.
- **Atendimento mais rápido aos problemas:** o relato do tutor e os alertas dos sensores entram numa fila única, com prioridade e responsável.
- **Tutor mais engajado e fiel:** ele vê o histórico, as orientações e quem está cuidando do caso do pet.
- **Entrega contínua e segura:** cada mudança no código é testada e publicada automaticamente, sem deploy manual e sem senha exposta no código.

### Stack

| Camada | Tecnologia |
|---|---|
| Aplicação | Java 21, Spring Boot 4, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Bean Validation |
| Banco de dados | Azure Database for PostgreSQL Flexible Server 16 (PaaS), com tabelas versionadas pelo Flyway |
| Testes | JUnit 5, Mockito e AssertJ (testes unitários das regras de negócio) |
| Build | Maven |
| Nuvem | Azure App Service: Plano Linux B1 e Web App Java 21 (Java SE), sem container |
| Infraestrutura | Azure CLI (Cloud Shell), em `scripts/criar-recursos.sh` |
| CI/CD | Azure DevOps: Pipelines YAML, Library (Variable group), Service connection e Environments |
| Código-fonte | GitHub, branch `master` |

---

## 2. Arquitetura e fluxo CI/CD

![Arquitetura da solução e fluxo CI/CD](docs/arquitetura.png)

| Nº | Etapa | Onde acontece |
|---|---|---|
| 0 | Criação dos recursos no Azure (uma vez) | Azure CLI no Cloud Shell |
| 1 | O desenvolvedor altera o código e faz push na `master` | IDE e GitHub |
| 2 | O push dispara a pipeline de CI | Azure DevOps, pipeline `clyvo-elo-ci` |
| 3 | O CI configura o Java 21, compila com Maven e roda os testes | Agente Ubuntu do Azure Pipelines |
| 4 | O CI publica o resultado dos testes e o artefato `app.jar` | Azure DevOps (aba Tests e Artifacts) |
| 5 | O artefato novo dispara a pipeline de CD | Azure DevOps, pipeline `clyvo-elo-cd` |
| 6 | O CD lê os dados do banco nas variáveis secretas | Library, Variable group `clyvo-elo-segredos` |
| 7 | O CD configura o Web App e faz o deploy do `app.jar` | Service connection `clyvo-elo-azure` e Azure Web App |
| 8 | O Web App sobe e o Flyway atualiza o banco | Azure Web App e Azure Database for PostgreSQL |
| 9 | Os usuários acessam por HTTPS e fazem o CRUD | Navegador |

---

## 3. Estrutura do repositório

```
azure-pipelines-ci.yml     pipeline de CI: build, testes e artefato
azure-pipelines-cd.yml     pipeline de CD: deploy no Azure Web App
script_bd.sql              DDL das tabelas com comentários
scripts/
  criar-recursos.sh        criação dos recursos no Azure com Azure CLI
  excluir-recursos.sh      remoção dos recursos ao final
  consultas-crud.sql       SELECTs usados para comprovar o CRUD no banco
docs/arquitetura.png       diagrama da arquitetura e do fluxo CI/CD
src/main/java              aplicação Spring Boot
src/main/resources/db/migration   migrações Flyway (as mesmas tabelas do script_bd.sql)
src/test/java              testes automatizados executados no CI
```

---

## 4. Banco de dados em nuvem

- **Serviço:** Azure Database for PostgreSQL, Flexible Server (PaaS), versão 16, plano Burstable B1ms.
- **Servidor:** `pg-clyvo-elo-rm563230.postgres.database.azure.com`, banco `clyvoelo`, conexão com SSL obrigatório.
- **Estrutura:** a DDL completa, com chaves e comentários em cada tabela e coluna, está em [`script_bd.sql`](script_bd.sql).
- **Criação das tabelas:** a aplicação roda as migrações do Flyway no primeiro start (`src/main/resources/db/migration`). Elas criam as tabelas e inserem dados de demonstração com conteúdo real: tutores, pets, eventos clínicos e alertas.

| Tabela | Conteúdo |
|---|---|
| `painel_tutor` | Tutores dos pets |
| `painel_pet` | Pets, cada um ligado a um tutor |
| `painel_evento_clinico` | Agenda de cuidados de cada pet |
| `painel_alerta` | Alertas de saúde e o responsável por eles |
| `painel_alerta_acompanhamento` | Linha do tempo do acompanhamento dos alertas |
| `painel_funcionario` | Equipe da clínica (administradores e veterinários) |

---

## 5. Pipelines

### CI — `azure-pipelines-ci.yml` (pipeline `clyvo-elo-ci`)

- **Gatilho:** todo push na branch `master`.
- **Agente:** `ubuntu-latest`, hospedado pela Microsoft.

| Etapa | O que faz |
|---|---|
| Configura o Java 21 | Seleciona o JDK 21 do agente |
| Compila e executa os testes automatizados | `mvn clean package`: compila e roda os testes JUnit. Se um teste falhar, a pipeline para e nada é publicado |
| Publica o resultado dos testes | Os relatórios do Surefire vão para a aba **Tests** da execução |
| Separa o pacote e renomeia para `app.jar` | Copia o `.jar` gerado para a área de artefatos |
| Publica o artefato no Azure DevOps | Publica o artefato `clyvo-elo-painel` na execução |

### CD — `azure-pipelines-cd.yml` (pipeline `clyvo-elo-cd`)

- **Gatilho:** conclusão com sucesso do `clyvo-elo-ci` na `master`, ou seja, um artefato novo (`resources.pipelines` com `trigger`).
- **Ambiente:** `producao`, onde o Azure DevOps guarda o histórico de deploys.

| Etapa | O que faz |
|---|---|
| Baixa o artefato gerado pelo CI | Baixa o `app.jar` da execução de CI que disparou o CD |
| Configura as variáveis de ambiente do Web App | Grava no Web App a URL, o usuário e a senha do banco, lidos das variáveis secretas |
| Faz o deploy do `app.jar` no Web App | Publica o pacote no Web App Java 21 pela service connection |

### Variáveis protegidas

Nenhum usuário, senha ou token fica no código. Os dados do banco ficam no Variable group **`clyvo-elo-segredos`** (Pipelines → Library), com a senha marcada como **secreta**: ela aparece como `***` nos logs. O acesso ao Azure é feito pela **service connection** `clyvo-elo-azure`, sem credencial no repositório. No código, a aplicação lê as variáveis de ambiente `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.

---

## 6. Como fazer o deploy (How To)

### 6.1 Pré-requisitos

- Assinatura do Azure ativa (por exemplo, Azure for Students)
- Organização no Azure DevOps com execução de pipelines liberada
- Conta no GitHub com acesso a este repositório

### 6.2 Criar os recursos no Azure (Azure CLI)

No [Portal do Azure](https://portal.azure.com), abra o **Cloud Shell** em modo **Bash** e rode:

```bash
git clone https://github.com/GabrielCabralmm/clyvo-elo-devops.git
cd clyvo-elo-devops
export MEU_IP=<seu IP público, para consultar o banco do seu computador>
bash scripts/criar-recursos.sh
```

O script pede a senha do administrador do banco e cria, em ordem:

| Recurso | Nome |
|---|---|
| Grupo de recursos | `rg-clyvo-elo` |
| Azure Database for PostgreSQL Flexible Server | `pg-clyvo-elo-rm563230` (usuário `clyvoadmin`) |
| Banco de dados | `clyvoelo` |
| Regras de firewall | acesso a partir dos serviços do Azure, mais o `MEU_IP` |
| Plano do Serviço de Aplicativo | `plan-clyvo-elo` (Linux, B1) |
| Serviço de Aplicativo | `clyvo-elo-rm563230` (Java 21) |
| Configurações adicionais | Always On, somente HTTPS, logs, comando de inicialização e porta |

Para usar outra região, rode `export LOCAL=eastus2` antes do script. O padrão é `brazilsouth`.

### 6.3 Configurar o Azure DevOps

1. **Criar o projeto:** em Azure DevOps → **New project**:
   - **Project name:** `Sprint 4 – Azure DevOps`
   - **Description:** `Projeto para entrega da Sprint 4 do professor <nome do professor>` e, nas linhas seguintes, `RM563230 - Gabriel Cabral Mendes Mariano - 2TDSPO` e `RM566063 - João Victor Santana dos Santos - 2TDSPX`
   - **Visibility:** Private · **Version control:** Git · **Work item process:** Scrum
2. **Convidar o professor:** em Organization settings → **Users** → **Add users**, coloque o e-mail do professor com **Access level: Basic** e adicione-o ao projeto como **Contributor**.
3. **Service connection:** em Project settings → **Service connections** → **New** → **Azure Resource Manager** → **Workload Identity federation (automatic)**:
   - **Subscription:** a sua assinatura · **Resource group:** `rg-clyvo-elo`
   - **Name:** `clyvo-elo-azure`
   - Marque **Grant access permission to all pipelines**.
4. **Variáveis protegidas:** em Pipelines → **Library** → **+ Variable group**:
   - **Nome:** `clyvo-elo-segredos`
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://pg-clyvo-elo-rm563230.postgres.database.azure.com:5432/clyvoelo?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME` = `clyvoadmin`
   - `SPRING_DATASOURCE_PASSWORD` = a senha do banco, com o **cadeado** clicado para ficar secreta
   - Em **Pipeline permissions**, libere o grupo para as pipelines (ou autorize na primeira execução).
5. **Pipeline de CI:** Pipelines → **New pipeline** → **GitHub (YAML)** → este repositório → **Existing Azure Pipelines YAML file** → `/azure-pipelines-ci.yml` → **Save**. Depois, em **⋮ → Rename/move**, use o nome **`clyvo-elo-ci`**.
6. **Pipeline de CD:** repita o passo 5 com `/azure-pipelines-cd.yml` e o nome **`clyvo-elo-cd`**. O nome do CI precisa ser exatamente `clyvo-elo-ci`, porque é ele que dispara o CD.
7. **Primeira execução:** rode o `clyvo-elo-ci`. Ao terminar, o `clyvo-elo-cd` começa sozinho. Na primeira vez, clique em **Permit** para liberar o Variable group, a service connection e o ambiente `producao`.

### 6.4 Testar a esteira

1. Altere um texto visível no código, por exemplo o título da tela de login em `src/main/resources/templates/login.html`.
2. Faça `git commit` e `git push` na `master`.
3. Em Pipelines, o `clyvo-elo-ci` inicia sozinho. Confira os testes na aba **Tests** e o artefato `clyvo-elo-painel` em **Artifacts**.
4. Ao terminar o CI, o `clyvo-elo-cd` inicia sozinho e faz o deploy.
5. Acesse https://clyvo-elo-rm563230.azurewebsites.net/login e confira a alteração. A primeira resposta depois do deploy pode levar cerca de 1 minuto.

---

## 7. Como testar o CRUD com evidência no banco

Use qualquer cliente PostgreSQL conectado ao servidor: pgAdmin, DBeaver, Azure Data Studio ou a extensão PostgreSQL do VS Code. O IP precisa estar liberado no firewall. Os SELECTs estão em [`scripts/consultas-crud.sql`](scripts/consultas-crud.sql).

Entre como **Administrador** (`renata.souza@clyvovet.com` / `clyvo123`):

| Operação | Na aplicação | No banco |
|---|---|---|
| **Inserir** | Tutores → Novo tutor. Depois Pets → Novo pet, escolhendo esse tutor | `SELECT * FROM painel_tutor ORDER BY id_tutor DESC;` e `SELECT * FROM painel_pet ORDER BY id_pet DESC;` |
| **Consultar** | Listas de tutores e pets, com busca, e a ficha do pet | `SELECT` com `JOIN` de tutores e pets |
| **Atualizar** | Pets → Editar: troque a raça e o peso. Tutores → Editar: troque o telefone | `SELECT` do pet e do tutor alterados |
| **Excluir** | Na lista de pets, Excluir o pet criado; depois, na lista de tutores, Excluir o tutor | `SELECT COUNT(*)` mostrando que os registros não existem mais |

O painel não deixa excluir um tutor que ainda tem pets nem um pet com histórico clínico, para não perder dados de saúde. Por isso, a exclusão é feita no pet recém-criado e depois no tutor dele.

### Acessos de demonstração

Todos usam a senha `clyvo123`.

| Perfil | E-mail |
|---|---|
| Administrador | renata.souza@clyvovet.com |
| Veterinário | ana.ferreira@clyvovet.com |
| Tutor | gabriel.mariano@email.com |

---

## 8. Rodar localmente (opcional)

Com um PostgreSQL local e o banco `clyvoelo` criado:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/clyvoelo
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=<sua senha>
mvn spring-boot:run
```

Acesse http://localhost:8080. Para rodar só os testes, use `mvn test`.

## 9. Remover os recursos

Depois da avaliação, rode `bash scripts/excluir-recursos.sh` no Cloud Shell para apagar o grupo de recursos e parar a cobrança.
