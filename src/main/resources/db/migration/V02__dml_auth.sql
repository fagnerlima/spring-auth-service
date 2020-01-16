DO $$
DECLARE
  _permissoes VARCHAR[] := ARRAY[
    ['ROLE_ADMIN', 'Administrador'],
    ['ROLE_SYSTEM', 'Sistema'],
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
    (1, 'Administrador', 'admin@email.com', 'admin', '$2a$10$NjHXi93qH9xoxYjk4nGiauYwVcK5qQTDxl7itopsOixNhL05396.K', 0, NOW(), FALSE, FALSE, NOW(), 'admin', NOW(), 'admin');
  INSERT INTO auth.grupo (id, nome, created_at, created_by, updated_at, updated_by) VALUES
    (1, 'Administrador', NOW(), 'admin', NOW(), 'admin');
  INSERT INTO auth.usuario_grupo (id_usuario, id_grupo) VALUES
    (1, 1);

  ALTER SEQUENCE auth.usuario_id_seq RESTART WITH 2;
  ALTER SEQUENCE auth.grupo_id_seq RESTART WITH 2;

  -- Permissões
  FOREACH _permissao SLICE 1 IN ARRAY _permissoes
  LOOP
    INSERT INTO auth.permissao (papel, descricao) VALUES
      (_permissao[1], _permissao[2]);
  END LOOP;

  INSERT INTO auth.grupo_permissao (id_grupo, id_permissao) VALUES
    (1, (SELECT id FROM auth.permissao WHERE papel = 'ROLE_ADMIN'));
END $$;
