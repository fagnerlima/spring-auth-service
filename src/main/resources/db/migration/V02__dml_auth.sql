DO $$
DECLARE
  _permissoes VARCHAR[] := ARRAY[
    ['ROLE_ADMIN', 'Administrador'],
    ['ROLE_GRUPO_LISTAR', 'Grupo - Listar'],
    ['ROLE_GRUPO_BUSCAR', 'Grupo - Buscar'],
    ['ROLE_GRUPO_SALVAR', 'Grupo - Salvar'],
    ['ROLE_GRUPO_EDITAR', 'Grupo - Editar'],
    ['ROLE_GRUPO_ALTERAR_STATUS', 'Grupo - Alterar status'],
    ['ROLE_USUARIO_LISTAR', 'Usuário - Listar'],
    ['ROLE_USUARIO_BUSCAR', 'Usuário - Buscar'],
    ['ROLE_USUARIO_SALVAR', 'Usuário - Salvar'],
    ['ROLE_USUARIO_EDITAR', 'Usuário - Editar'],
    ['ROLE_USUARIO_ALTERAR_STATUS', 'Usuário - Alterar status'],
    ['ROLE_PERMISSAO_LISTAR', 'Permissão - Listar'],
    ['ROLE_PERMISSAO_BUSCAR', 'Permissão - Buscar']
  ];
  _permissao VARCHAR[];
BEGIN
  -- Usuário e Grupo Administrador
  INSERT INTO auth.usuario (id, nome, email, login, valor_senha, tentativas_erro_senha, data_atualizacao_senha, pendente, bloqueado, created_at, created_by, updated_at, updated_by) VALUES
    (-1, 'Super User', 'root@email.com', 'root', '$2a$10$ruzUEX3kN/b0URwAioSWXOQ5FPeK8LttYNveZywgiFQfq0wI0EVK6', 0, NOW(), FALSE, FALSE, NOW(), 'root', NOW(), 'root'),
    (-2, 'System User', 'system@email.com', 'system', '$2a$10$2hCHsFoN3kyZNNnDwb4e9.5tGGs2tmCh46YKiCRVn18ghDMDgIMZm', 0, NOW(), FALSE, FALSE, NOW(), 'root', NOW(), 'root'),
    (1, 'Administrador', 'admin@email.com', 'admin', '$2a$10$9.vsponXOmbP6L9RqeZPBeVhojwg4bhjLdVTP/EkLaZGht9juv5fq', 0, NOW(), FALSE, FALSE, NOW(), 'root', NOW(), 'root');
  INSERT INTO auth.grupo (id, nome, created_at, created_by, updated_at, updated_by) VALUES
    (-1, 'Root', NOW(), 'root', NOW(), 'root'),
    (-2, 'System', NOW(), 'root', NOW(), 'root'),
    (1, 'Administrador', NOW(), 'root', NOW(), 'root');
  INSERT INTO auth.usuario_grupo (id_usuario, id_grupo) VALUES
    (-1, -1),
    (-2, -2),
    (1, 1);

  ALTER SEQUENCE auth.usuario_id_seq RESTART WITH 2;
  ALTER SEQUENCE auth.grupo_id_seq RESTART WITH 2;

  -- Permissões
  INSERT INTO auth.permissao (id, papel, descricao) VALUES
    (-1, 'ROLE_ROOT', 'Root'),
    (-2, 'ROLE_SYSTEM', 'System');

  FOREACH _permissao SLICE 1 IN ARRAY _permissoes
  LOOP
    INSERT INTO auth.permissao (papel, descricao) VALUES
      (_permissao[1], _permissao[2]);
  END LOOP;

  INSERT INTO auth.grupo_permissao (id_grupo, id_permissao) VALUES
    (-1, -1),
    (-2, -2),
    (1, (SELECT id FROM auth.permissao WHERE papel = 'ROLE_ADMIN'));
END $$;
