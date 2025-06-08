-- Addresses
ALTER TABLE addresses RENAME TO addresses_old;

CREATE TABLE addresses (
  address TEXT NOT NULL PRIMARY KEY,
  nicknames TEXT,
  whitelisted INTEGER DEFAULT 0,
  blacklisted INTEGER DEFAULT 0
);

INSERT INTO addresses (address, nicknames, whitelisted, blacklisted)
SELECT address, nicknames, whitelisted, blacklisted FROM addresses_old;

DROP TABLE addresses_old;

-- Users
ALTER TABLE users RENAME TO users_old;

CREATE TABLE users (
  nickname TEXT NOT NULL PRIMARY KEY,
  addresses TEXT,
  whitelisted INTEGER DEFAULT 0,
  blacklisted INTEGER DEFAULT 0
);

INSERT INTO users (nickname, addresses, whitelisted, blacklisted)
SELECT nickname, addresses, whitelisted, blacklisted FROM users_old;

DROP TABLE users_old;