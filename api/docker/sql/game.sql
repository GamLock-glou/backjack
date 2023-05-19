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

-- TODO: split the lobbies table into lobbies and games tables
CREATE TABLE lobbies (
  id serial NOT NULL,
  PRIMARY KEY (id),
  room_name character varying NOT NULL,
  is_started boolean NOT NULL DEFAULT false,
  bets_id numeric[]
);

CREATE TABLE bets (
  id serial NOT NULL,
  PRIMARY KEY (id),
  user_id numeric NOT NULL,
  bet numeric NOT NULL,
  is_win boolean NOT NULL DEFAULT false
);

-- Users INSERT
INSERT INTO users (name, password, money, token)
VALUES ('Zack', '123456', 123, '123456');
INSERT INTO users (name, password, money, token)
VALUES ('Eugene', '56789', 1000, '56789');
INSERT INTO users (name, password, money, token)
VALUES ('Admin', 'root', 1000000, 'root');

-- Bets
INSERT INTO bets (user_id, bet, is_win)
VALUES (0, 20, true);
INSERT INTO bets (user_id, bet)
VALUES (0, 20);
INSERT INTO bets (user_id, bet, is_win)
VALUES (1, 500, true);

-- Lobbies
INSERT INTO lobbies (room_name, bets_id)
VALUES ('room_1', '{0, 1, 2}');
INSERT INTO lobbies (room_name)
VALUES ('room_3');
COMMIT;