DO $$
DECLARE
  _papeis VARCHAR[] := ARRAY[
    'ROLE_ADMIN',
    'ROLE_SYSTEM',
    'ROLE_GRUPO_LISTAR',
    'ROLE_GRUPO_BUSCAR',
    'ROLE_GRUPO_SALVAR',
    'ROLE_GRUPO_EDITAR',
    'ROLE_GRUPO_ALTERAR_STATUS',
    'ROLE_USUARIO_LISTAR',
    'ROLE_USUARIO_BUSCAR',
    'ROLE_USUARIO_SALVAR',
    'ROLE_USUARIO_EDITAR',
    'ROLE_USUARIO_ALTERAR_STATUS',
    'ROLE_PERMISSAO_LISTAR',
    'ROLE_PERMISSAO_BUSCAR'
  ];
  _papel VARCHAR;
BEGIN
  -- Usuário e Grupo Administrador
  INSERT INTO auth.usuario (id, nome, email, login, valor_senha, pendente, bloqueado, data_criacao, id_usuario_criacao, data_atualizacao, id_usuario_atualizacao) VALUES
    (1, 'Administrador', 'admin@email.com', 'admin', '$2a$10$NjHXi93qH9xoxYjk4nGiauYwVcK5qQTDxl7itopsOixNhL05396.K', FALSE, FALSE, NOW(), 1, NOW(), 1);
  INSERT INTO auth.grupo (id, nome, data_criacao, id_usuario_criacao, data_atualizacao, id_usuario_atualizacao) VALUES
    (1, 'Administrador', NOW(), 1, NOW(), 1);
  INSERT INTO auth.usuario_grupo (id_usuario, id_grupo) VALUES
    (1, 1);

  ALTER SEQUENCE auth.usuario_id_seq RESTART WITH 2;
  ALTER SEQUENCE auth.grupo_id_seq RESTART WITH 2;

  -- Permissões
  FOREACH _papel IN ARRAY _papeis
  LOOP
    INSERT INTO auth.permissao (papel, data_criacao, id_usuario_criacao, data_atualizacao, id_usuario_atualizacao) VALUES
      (_papel, NOW(), 1, NOW(), 1);
  END LOOP;

  INSERT INTO auth.grupo_permissao (id_grupo, id_permissao) VALUES
    (1, (SELECT id FROM auth.permissao WHERE papel = 'ROLE_ADMIN'));
END $$;
