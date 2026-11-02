CREATE TABLE painel_funcionario (
    id_funcionario BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    data_cadastro DATE DEFAULT CURRENT_DATE NOT NULL,
    CONSTRAINT painel_ck_funcionario_perfil CHECK (perfil IN ('ADMIN', 'DOUTOR'))
);

CREATE TABLE painel_pet (
    id_pet BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    especie VARCHAR(20) NOT NULL,
    raca VARCHAR(80),
    peso_kg NUMERIC(5,2),
    nome_tutor VARCHAR(120) NOT NULL,
    CONSTRAINT painel_ck_pet_especie CHECK (especie IN ('CACHORRO', 'GATO'))
);

CREATE TABLE painel_evento_clinico (
    id_evento BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pet BIGINT NOT NULL,
    tipo_evento VARCHAR(30) NOT NULL,
    descricao VARCHAR(500),
    data_evento DATE DEFAULT CURRENT_DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDENTE' NOT NULL,
    observacao_atendimento VARCHAR(500),
    id_funcionario_atendeu BIGINT,
    CONSTRAINT painel_ck_evento_tipo CHECK (tipo_evento IN ('VACINA','CHECKUP','POS_OPERATORIO','MEDICACAO','EXAME')),
    CONSTRAINT painel_ck_evento_status CHECK (status IN ('PENDENTE','CONCLUIDO','ATRASADO')),
    CONSTRAINT painel_fk_evento_pet FOREIGN KEY (id_pet) REFERENCES painel_pet(id_pet),
    CONSTRAINT painel_fk_evento_funcionario FOREIGN KEY (id_funcionario_atendeu) REFERENCES painel_funcionario(id_funcionario)
);

CREATE TABLE painel_alerta (
    id_alerta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pet BIGINT NOT NULL,
    origem VARCHAR(30) NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    status VARCHAR(20) DEFAULT 'ABERTO' NOT NULL,
    data_criacao DATE DEFAULT CURRENT_DATE NOT NULL,
    nota_resolucao VARCHAR(500),
    id_funcionario_resolveu BIGINT,
    data_resolucao DATE,
    CONSTRAINT painel_ck_alerta_prioridade CHECK (prioridade IN ('BAIXA','MEDIA','ALTA')),
    CONSTRAINT painel_ck_alerta_status CHECK (status IN ('ABERTO','RESOLVIDO')),
    CONSTRAINT painel_fk_alerta_pet FOREIGN KEY (id_pet) REFERENCES painel_pet(id_pet),
    CONSTRAINT painel_fk_alerta_funcionario FOREIGN KEY (id_funcionario_resolveu) REFERENCES painel_funcionario(id_funcionario)
);
