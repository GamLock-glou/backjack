CREATE DATABASE gamedb;
\c gamedb;

-- Directors
CREATE TABLE users (
  id serial NOT NULL,
  PRIMARY KEY (id),
  name character varying NOT NULL,
  password character varying NOT NULL,
  money decimal NOT NULL,
  token character varying NOT NULL
);

CREATE TABLE lobbies (
  id serial NOT NULL,
  PRIMARY KEY (id),
  room_name character varying NOT NULL,
  limit_users integer NOT NULL DEFAULT 1,
  users_count integer NOT NULL DEFAULT 0
);

CREATE TABLE lobbies_users_bets (
  id serial NOT NULL,
  PRIMARY KEY (id),
  user_id integer NOT NULL,
  lobby_id integer NOT NULL,
  bet_id integer
);

CREATE TABLE bets (
  id serial NOT NULL,
  PRIMARY KEY (id),
  user_id integer NOT NULL,
  lobby_id integer NOT NULL,
  bet decimal NOT NULL DEFAULT 0,
  is_win boolean NOT NULL DEFAULT false
);

-- Users INSERT
INSERT INTO users (name, password, money, token)
VALUES ('Zack', '123456', 123, '123456');
INSERT INTO users (name, password, money, token)
VALUES ('Eugene', '56789', 1000, '56789');
INSERT INTO users (name, password, money, token)
VALUES ('Admin', 'root', 1000000, 'root');

-- Lobbies
INSERT INTO lobbies (room_name, limit_users)
VALUES ('room_1', 1);
INSERT INTO lobbies (room_name, limit_users)
VALUES ('room_2', 3);
INSERT INTO lobbies (room_name)
VALUES ('room_3');
COMMIT;

-- Bets
INSERT INTO bets (user_id, lobby_id, bet, is_win)
VALUES (1, 1, 50, true);
INSERT INTO bets (user_id, lobby_id, bet, is_win)
VALUES (1, 1, 50, false);
INSERT INTO bets (user_id, lobby_id, bet, is_win)
VALUES (2, 2, 500, true);

