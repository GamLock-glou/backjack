CREATE DATABASE gamedb;
\c gamedb;

-- Directors
CREATE TABLE users (
  id serial NOT NULL,
  PRIMARY KEY (id),
  name character varying NOT NULL,
  password character varying NOT NULL,
  money numeric NOT NULL,
  token character varying NOT NULL
);

-- Directors
INSERT INTO users (name, password, money, token)
VALUES ('Zack', '123456', 123, '123456');
COMMIT;