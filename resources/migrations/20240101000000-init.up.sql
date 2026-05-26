CREATE TABLE users (
  id         BIGSERIAL    PRIMARY KEY,
  email      TEXT         NOT NULL UNIQUE,
  password   TEXT         NOT NULL,
  role       TEXT         NOT NULL DEFAULT 'user',
  created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
--;;
CREATE TABLE sessions (
  session_id       TEXT   NOT NULL PRIMARY KEY,
  idle_timeout     BIGINT,
  absolute_timeout BIGINT,
  value            BYTEA
);
--;;
CREATE TABLE items (
  id          BIGSERIAL   PRIMARY KEY,
  name        TEXT        NOT NULL,
  description TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
--;;
INSERT INTO items (name, description) VALUES
  ('Item One',   'First sample item'),
  ('Item Two',   'Second sample item'),
  ('Item Three', 'Third sample item');
