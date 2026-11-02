-- CLYVO Elo - Painel da Clínica
-- Banco: Azure Database for PostgreSQL (Flexible Server)
-- As mesmas tabelas são criadas automaticamente pelo Flyway no primeiro start da aplicação (src/main/resources/db/migration)

-- Funcionários da clínica
CREATE TABLE painel_funcionario (
    id_funcionario BIGINT GENERATED ALWAYS AS IDENTITY,
    nome           VARCHAR(120) NOT NULL,
    email          VARCHAR(150) NOT NULL,
    senha_hash     VARCHAR(255) NOT NULL,
    perfil         VARCHAR(20)  NOT NULL,
    data_cadastro  DATE DEFAULT CURRENT_DATE NOT NULL,
    CONSTRAINT painel_funcionario_pkey PRIMARY KEY (id_funcionario),
    CONSTRAINT painel_funcionario_email_key UNIQUE (email),
    CONSTRAINT painel_ck_funcionario_perfil CHECK (perfil IN ('ADMIN', 'DOUTOR'))
);

COMMENT ON TABLE  painel_funcionario                IS 'Equipe da clínica com acesso ao painel (administradores e veterinários)';
COMMENT ON COLUMN painel_funcionario.id_funcionario IS 'Identificador do funcionário (chave primária)';
COMMENT ON COLUMN painel_funcionario.nome           IS 'Nome completo do funcionário';
COMMENT ON COLUMN painel_funcionario.email          IS 'E-mail de acesso ao painel, único entre os funcionários';
COMMENT ON COLUMN painel_funcionario.senha_hash     IS 'Senha criptografada com BCrypt';
COMMENT ON COLUMN painel_funcionario.perfil         IS 'Perfil de acesso: ADMIN ou DOUTOR';
COMMENT ON COLUMN painel_funcionario.data_cadastro  IS 'Data de cadastro do funcionário';

-- Tutores (donos dos pets)
CREATE TABLE painel_tutor (
    id_tutor      BIGINT GENERATED ALWAYS AS IDENTITY,
    nome          VARCHAR(120) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    senha_hash    VARCHAR(255) NOT NULL,
    telefone      VARCHAR(20),
    data_cadastro DATE DEFAULT CURRENT_DATE NOT NULL,
    CONSTRAINT painel_tutor_pkey PRIMARY KEY (id_tutor),
    CONSTRAINT painel_tutor_email_key UNIQUE (email)
);

COMMENT ON TABLE  painel_tutor               IS 'Tutores responsáveis pelos pets atendidos pela clínica';
COMMENT ON COLUMN painel_tutor.id_tutor      IS 'Identificador do tutor (chave primária)';
COMMENT ON COLUMN painel_tutor.nome          IS 'Nome completo do tutor';
COMMENT ON COLUMN painel_tutor.email         IS 'E-mail de acesso do tutor, único';
COMMENT ON COLUMN painel_tutor.senha_hash    IS 'Senha criptografada com BCrypt';
COMMENT ON COLUMN painel_tutor.telefone      IS 'Telefone de contato do tutor';
COMMENT ON COLUMN painel_tutor.data_cadastro IS 'Data de cadastro do tutor';

-- Pets
CREATE TABLE painel_pet (
    id_pet          BIGINT GENERATED ALWAYS AS IDENTITY,
    nome            VARCHAR(80) NOT NULL,
    especie         VARCHAR(20) NOT NULL,
    raca            VARCHAR(80),
    peso_kg         NUMERIC(5,2),
    id_tutor        BIGINT NOT NULL,
    data_nascimento DATE,
    CONSTRAINT painel_pet_pkey PRIMARY KEY (id_pet),
    CONSTRAINT painel_ck_pet_especie CHECK (especie IN ('CACHORRO', 'GATO')),
    CONSTRAINT painel_fk_pet_tutor FOREIGN KEY (id_tutor) REFERENCES painel_tutor (id_tutor)
);

COMMENT ON TABLE  painel_pet                 IS 'Pets acompanhados pela clínica (tabela central da solução)';
COMMENT ON COLUMN painel_pet.id_pet          IS 'Identificador do pet (chave primária)';
COMMENT ON COLUMN painel_pet.nome            IS 'Nome do pet';
COMMENT ON COLUMN painel_pet.especie         IS 'Espécie: CACHORRO ou GATO';
COMMENT ON COLUMN painel_pet.raca            IS 'Raça do pet';
COMMENT ON COLUMN painel_pet.peso_kg         IS 'Peso atual em quilos';
COMMENT ON COLUMN painel_pet.id_tutor        IS 'Tutor responsável (chave estrangeira para painel_tutor)';
COMMENT ON COLUMN painel_pet.data_nascimento IS 'Data de nascimento, usada para calcular a idade';

-- Eventos clínicos (agenda de cuidados de cada pet)
CREATE TABLE painel_evento_clinico (
    id_evento              BIGINT GENERATED ALWAYS AS IDENTITY,
    id_pet                 BIGINT NOT NULL,
    tipo_evento            VARCHAR(30) NOT NULL,
    descricao              VARCHAR(500),
    data_evento            DATE DEFAULT CURRENT_DATE NOT NULL,
    status                 VARCHAR(20) DEFAULT 'PENDENTE' NOT NULL,
    observacao_atendimento VARCHAR(500),
    id_funcionario_atendeu BIGINT,
    data_atendimento       DATE,
    CONSTRAINT painel_evento_clinico_pkey PRIMARY KEY (id_evento),
    CONSTRAINT painel_ck_evento_tipo CHECK (tipo_evento IN ('VACINA', 'CHECKUP', 'POS_OPERATORIO', 'MEDICACAO', 'EXAME')),
    CONSTRAINT painel_ck_evento_status CHECK (status IN ('PENDENTE', 'CONCLUIDO', 'ATRASADO')),
    CONSTRAINT painel_fk_evento_pet FOREIGN KEY (id_pet) REFERENCES painel_pet (id_pet),
    CONSTRAINT painel_fk_evento_funcionario FOREIGN KEY (id_funcionario_atendeu) REFERENCES painel_funcionario (id_funcionario)
);

COMMENT ON TABLE  painel_evento_clinico                        IS 'Cuidados agendados e realizados para cada pet: vacinas, check-ups, pós-operatórios, medicações e exames';
COMMENT ON COLUMN painel_evento_clinico.id_evento              IS 'Identificador do evento (chave primária)';
COMMENT ON COLUMN painel_evento_clinico.id_pet                 IS 'Pet do evento (chave estrangeira para painel_pet)';
COMMENT ON COLUMN painel_evento_clinico.tipo_evento            IS 'Tipo: VACINA, CHECKUP, POS_OPERATORIO, MEDICACAO ou EXAME';
COMMENT ON COLUMN painel_evento_clinico.descricao              IS 'Descrição do cuidado';
COMMENT ON COLUMN painel_evento_clinico.data_evento            IS 'Data prevista do cuidado';
COMMENT ON COLUMN painel_evento_clinico.status                 IS 'Situação: PENDENTE, CONCLUIDO ou ATRASADO';
COMMENT ON COLUMN painel_evento_clinico.observacao_atendimento IS 'Anotação do veterinário no atendimento';
COMMENT ON COLUMN painel_evento_clinico.id_funcionario_atendeu IS 'Veterinário que atendeu (chave estrangeira para painel_funcionario)';
COMMENT ON COLUMN painel_evento_clinico.data_atendimento       IS 'Data em que o evento foi atendido';

-- Alertas de saúde
CREATE TABLE painel_alerta (
    id_alerta                  BIGINT GENERATED ALWAYS AS IDENTITY,
    id_pet                     BIGINT NOT NULL,
    origem                     VARCHAR(30) NOT NULL,
    prioridade                 VARCHAR(20) NOT NULL,
    descricao                  VARCHAR(500) NOT NULL,
    status                     VARCHAR(20) DEFAULT 'ABERTO' NOT NULL,
    data_criacao               DATE DEFAULT CURRENT_DATE NOT NULL,
    nota_resolucao             VARCHAR(500),
    id_funcionario_resolveu    BIGINT,
    data_resolucao             DATE,
    id_funcionario_responsavel BIGINT,
    id_tutor_relatou           BIGINT,
    CONSTRAINT painel_alerta_pkey PRIMARY KEY (id_alerta),
    CONSTRAINT painel_ck_alerta_prioridade CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA')),
    CONSTRAINT painel_ck_alerta_status CHECK (status IN ('ABERTO', 'EM_ACOMPANHAMENTO', 'RESOLVIDO')),
    CONSTRAINT painel_fk_alerta_pet FOREIGN KEY (id_pet) REFERENCES painel_pet (id_pet),
    CONSTRAINT painel_fk_alerta_funcionario FOREIGN KEY (id_funcionario_resolveu) REFERENCES painel_funcionario (id_funcionario),
    CONSTRAINT painel_fk_alerta_responsavel FOREIGN KEY (id_funcionario_responsavel) REFERENCES painel_funcionario (id_funcionario),
    CONSTRAINT painel_fk_alerta_tutor FOREIGN KEY (id_tutor_relatou) REFERENCES painel_tutor (id_tutor)
);

COMMENT ON TABLE  painel_alerta                            IS 'Alertas de saúde do pet, relatados pelo tutor ou gerados por sensores, acompanhados pela equipe';
COMMENT ON COLUMN painel_alerta.id_alerta                  IS 'Identificador do alerta (chave primária)';
COMMENT ON COLUMN painel_alerta.id_pet                     IS 'Pet do alerta (chave estrangeira para painel_pet)';
COMMENT ON COLUMN painel_alerta.origem                     IS 'Origem do alerta (relato do tutor, sensor IoT, consulta)';
COMMENT ON COLUMN painel_alerta.prioridade                 IS 'Prioridade: BAIXA, MEDIA ou ALTA';
COMMENT ON COLUMN painel_alerta.descricao                  IS 'Descrição do problema relatado';
COMMENT ON COLUMN painel_alerta.status                     IS 'Situação: ABERTO, EM_ACOMPANHAMENTO ou RESOLVIDO';
COMMENT ON COLUMN painel_alerta.data_criacao               IS 'Data de abertura do alerta';
COMMENT ON COLUMN painel_alerta.nota_resolucao             IS 'Conduta registrada na resolução';
COMMENT ON COLUMN painel_alerta.id_funcionario_resolveu    IS 'Funcionário que resolveu (chave estrangeira para painel_funcionario)';
COMMENT ON COLUMN painel_alerta.data_resolucao             IS 'Data da resolução';
COMMENT ON COLUMN painel_alerta.id_funcionario_responsavel IS 'Veterinário responsável pelo acompanhamento';
COMMENT ON COLUMN painel_alerta.id_tutor_relatou           IS 'Tutor que relatou o alerta, quando a origem é o tutor';

-- Histórico de acompanhamento dos alertas
CREATE TABLE painel_alerta_acompanhamento (
    id_acompanhamento BIGINT GENERATED ALWAYS AS IDENTITY,
    id_alerta         BIGINT NOT NULL,
    autor             VARCHAR(120) NOT NULL,
    anotacao          VARCHAR(500) NOT NULL,
    data_registro     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT painel_alerta_acompanhamento_pkey PRIMARY KEY (id_acompanhamento),
    CONSTRAINT painel_fk_acompanhamento_alerta FOREIGN KEY (id_alerta) REFERENCES painel_alerta (id_alerta)
);

COMMENT ON TABLE  painel_alerta_acompanhamento                   IS 'Linha do tempo de anotações feitas durante o acompanhamento de um alerta';
COMMENT ON COLUMN painel_alerta_acompanhamento.id_acompanhamento IS 'Identificador da anotação (chave primária)';
COMMENT ON COLUMN painel_alerta_acompanhamento.id_alerta         IS 'Alerta acompanhado (chave estrangeira para painel_alerta)';
COMMENT ON COLUMN painel_alerta_acompanhamento.autor             IS 'Nome de quem registrou a anotação';
COMMENT ON COLUMN painel_alerta_acompanhamento.anotacao          IS 'Texto da anotação';
COMMENT ON COLUMN painel_alerta_acompanhamento.data_registro     IS 'Data e hora do registro';
