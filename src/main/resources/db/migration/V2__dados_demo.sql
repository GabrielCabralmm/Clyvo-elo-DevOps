INSERT INTO painel_funcionario (nome, email, senha_hash, perfil) VALUES
    ('Ana Ferreira', 'ana.ferreira@clyvovet.com', '$2b$10$nSigTEiBHI5Oqe4q0AJcAukZ9oUOjeYptHi51so4dIJIhorZ5LciS', 'DOUTOR');
INSERT INTO painel_funcionario (nome, email, senha_hash, perfil) VALUES
    ('Carlos Mendes', 'carlos.mendes@clyvovet.com', '$2b$10$nSigTEiBHI5Oqe4q0AJcAukZ9oUOjeYptHi51so4dIJIhorZ5LciS', 'DOUTOR');
INSERT INTO painel_funcionario (nome, email, senha_hash, perfil) VALUES
    ('Renata Souza', 'renata.souza@clyvovet.com', '$2b$10$nSigTEiBHI5Oqe4q0AJcAukZ9oUOjeYptHi51so4dIJIhorZ5LciS', 'ADMIN');

INSERT INTO painel_pet (nome, especie, raca, peso_kg, nome_tutor) VALUES
    ('Toby', 'CACHORRO', 'Golden Retriever', 28.4, 'Gabriel Mariano');
INSERT INTO painel_pet (nome, especie, raca, peso_kg, nome_tutor) VALUES
    ('Mimi', 'GATO', 'SRD', 4.1, 'Marina Alves');
INSERT INTO painel_pet (nome, especie, raca, peso_kg, nome_tutor) VALUES
    ('Nina', 'GATO', 'Siames', 3.8, 'Juliana Prado');
INSERT INTO painel_pet (nome, especie, raca, peso_kg, nome_tutor) VALUES
    ('Bento', 'CACHORRO', 'Vira-lata', 22.0, 'Eduardo Carvalho');

INSERT INTO painel_evento_clinico (id_pet, tipo_evento, descricao, status) VALUES
    (1, 'VACINA', 'Vacina antirrábica anual', 'PENDENTE');
INSERT INTO painel_evento_clinico (id_pet, tipo_evento, descricao, status) VALUES
    (2, 'CHECKUP', 'Check-up de rotina semestral', 'PENDENTE');
INSERT INTO painel_evento_clinico (id_pet, tipo_evento, descricao, status) VALUES
    (3, 'EXAME', 'Exame de sangue pre-operatorio', 'CONCLUIDO');

INSERT INTO painel_alerta (id_pet, origem, prioridade, descricao, status) VALUES
    (1, 'SENSOR_IOT', 'ALTA', 'Temperatura corporal acima do normal detectada pelo dispositivo', 'ABERTO');
INSERT INTO painel_alerta (id_pet, origem, prioridade, descricao, status) VALUES
    (4, 'MANUAL', 'MEDIA', 'Tutor relatou baixa ingestao de agua nas ultimas 24h', 'ABERTO');
INSERT INTO painel_alerta (id_pet, origem, prioridade, descricao, status, nota_resolucao, id_funcionario_resolveu, data_resolucao) VALUES
    (2, 'SENSOR_IOT', 'BAIXA', 'Atividade fisica abaixo da media semanal', 'RESOLVIDO', 'Contato com tutor, pet em recuperacao de procedimento, atividade reduzida esperada.', 1, CURRENT_DATE - 2);
