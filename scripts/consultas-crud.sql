-- Consulta: tutores e seus pets
SELECT t.id_tutor, t.nome AS tutor, t.email, p.id_pet, p.nome AS pet, p.especie, p.raca, p.peso_kg
FROM painel_tutor t
LEFT JOIN painel_pet p ON p.id_tutor = t.id_tutor
ORDER BY t.id_tutor, p.id_pet;

-- Inserção: tutor e pet cadastrados pela aplicação
SELECT * FROM painel_tutor ORDER BY id_tutor DESC LIMIT 3;
SELECT * FROM painel_pet ORDER BY id_pet DESC LIMIT 3;

-- Atualização: conferir o pet alterado (troque o nome)
SELECT id_pet, nome, raca, peso_kg, id_tutor FROM painel_pet WHERE nome ILIKE '%nome do pet%';

-- Exclusão: o pet e o tutor não devem mais aparecer
SELECT COUNT(*) AS pets_com_o_nome FROM painel_pet WHERE nome ILIKE '%nome do pet%';
SELECT COUNT(*) AS tutores_com_o_email FROM painel_tutor WHERE email ILIKE '%email do tutor%';

-- Eventos clínicos dos pets
SELECT e.id_evento, p.nome AS pet, e.tipo_evento, e.data_evento, e.status
FROM painel_evento_clinico e
JOIN painel_pet p ON p.id_pet = e.id_pet
ORDER BY e.data_evento;

-- Migrações aplicadas pela aplicação
SELECT version, description, installed_on, success FROM flyway_schema_history ORDER BY installed_rank;
