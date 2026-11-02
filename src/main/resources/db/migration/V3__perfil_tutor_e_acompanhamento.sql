-- Tutores
CREATE TABLE painel_tutor (
    id_tutor BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    data_cadastro DATE DEFAULT CURRENT_DATE NOT NULL
);

INSERT INTO painel_tutor (nome, email, senha_hash)
SELECT nome_tutor, LOWER(REPLACE(TRIM(nome_tutor), ' ', '.')) || '@email.com', '$2b$10$nSigTEiBHI5Oqe4q0AJcAukZ9oUOjeYptHi51so4dIJIhorZ5LciS'
FROM (SELECT DISTINCT nome_tutor FROM painel_pet) nomes;

-- Vinculo do pet com o tutor
ALTER TABLE painel_pet ADD COLUMN id_tutor BIGINT;
ALTER TABLE painel_pet ADD COLUMN data_nascimento DATE;

UPDATE painel_pet p SET id_tutor = (SELECT t.id_tutor FROM painel_tutor t WHERE t.nome = p.nome_tutor);

ALTER TABLE painel_pet ALTER COLUMN id_tutor SET NOT NULL;
ALTER TABLE painel_pet ADD CONSTRAINT painel_fk_pet_tutor FOREIGN KEY (id_tutor) REFERENCES painel_tutor(id_tutor);
ALTER TABLE painel_pet DROP COLUMN nome_tutor;

-- Atendimento de eventos clinicos
ALTER TABLE painel_evento_clinico ADD COLUMN data_atendimento DATE;

UPDATE painel_evento_clinico SET data_atendimento = data_evento WHERE status = 'CONCLUIDO';

-- Acompanhamento de alertas
ALTER TABLE painel_alerta DROP CONSTRAINT painel_ck_alerta_status;
ALTER TABLE painel_alerta ADD CONSTRAINT painel_ck_alerta_status CHECK (status IN ('ABERTO','EM_ACOMPANHAMENTO','RESOLVIDO'));
ALTER TABLE painel_alerta ADD COLUMN id_funcionario_responsavel BIGINT;
ALTER TABLE painel_alerta ADD COLUMN id_tutor_relatou BIGINT;
ALTER TABLE painel_alerta ADD CONSTRAINT painel_fk_alerta_responsavel FOREIGN KEY (id_funcionario_responsavel) REFERENCES painel_funcionario(id_funcionario);
ALTER TABLE painel_alerta ADD CONSTRAINT painel_fk_alerta_tutor FOREIGN KEY (id_tutor_relatou) REFERENCES painel_tutor(id_tutor);

CREATE TABLE painel_alerta_acompanhamento (
    id_acompanhamento BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_alerta BIGINT NOT NULL,
    autor VARCHAR(120) NOT NULL,
    anotacao VARCHAR(500) NOT NULL,
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT painel_fk_acompanhamento_alerta FOREIGN KEY (id_alerta) REFERENCES painel_alerta(id_alerta)
);
