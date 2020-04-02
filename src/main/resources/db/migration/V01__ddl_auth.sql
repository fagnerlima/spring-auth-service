CREATE EXTENSION unaccent;

CREATE SCHEMA auth;

CREATE TABLE auth.usuario (
  id SERIAL,
  nome VARCHAR(128) NOT NULL,
  email VARCHAR(128) NOT NULL,
  login VARCHAR(32) NOT NULL,
  valor_senha VARCHAR(64),
  reset_token_senha VARCHAR(255),
  tentativas_erro_senha SMALLINT,
  data_atualizacao_senha DATE,
  pendente BOOLEAN NOT NULL DEFAULT TRUE,
  bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by VARCHAR(32) NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_by VARCHAR(32) NOT NULL,
  version INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT pk_usuario PRIMARY KEY(id),
  CONSTRAINT uk_usuario_email UNIQUE(email),
  CONSTRAINT uk_usuario_login UNIQUE(login)
);

CREATE INDEX idx_usuario_ativo ON auth.usuario (ativo);

CREATE TABLE auth.grupo (
  id SERIAL,
  nome VARCHAR(128) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by VARCHAR(32) NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_by VARCHAR(32) NOT NULL,
  version INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT pk_grupo PRIMARY KEY(id)
);

CREATE INDEX idx_grupo_ativo ON auth.grupo (ativo);

CREATE TABLE auth.permissao (
  id SERIAL,
  papel VARCHAR(128) NOT NULL,
  descricao VARCHAR(128) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  version INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT pk_permissao PRIMARY KEY(id),
  CONSTRAINT uk_permissao_papel UNIQUE(papel)
);

CREATE TABLE auth.grupo_permissao (
  id_grupo INTEGER NOT NULL,
  id_permissao INTEGER NOT NULL,
  CONSTRAINT pk_grupo_permissao PRIMARY KEY(id_grupo, id_permissao),
  CONSTRAINT fk_grupo_permissao_id_grupo FOREIGN KEY(id_grupo) REFERENCES auth.grupo(id)
    MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_grupo_permissao_id_permissao FOREIGN KEY(id_permissao) REFERENCES auth.permissao(id)
    MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE auth.usuario_grupo (
  id_usuario INTEGER NOT NULL,
  id_grupo INTEGER NOT NULL,
  CONSTRAINT pk_usuario_grupo PRIMARY KEY(id_usuario, id_grupo),
  CONSTRAINT fk_usuario_grupo_id_usuario FOREIGN KEY(id_usuario) REFERENCES auth.usuario(id)
    MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_usuario_grupo_id_grupo FOREIGN KEY(id_grupo) REFERENCES auth.grupo(id)
    MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
