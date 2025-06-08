CREATE TABLE IF NOT EXISTS addresses (
  address TEXT NOT NULL PRIMARY KEY,
  nicknames TEXT,
  whitelisted INTEGER DEFAULT 0,
  blacklisted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS users (
  nickname TEXT NOT NULL PRIMARY KEY,
  addresses TEXT,
  whitelisted INTEGER DEFAULT 0,
  blacklisted INTEGER DEFAULT 0
);