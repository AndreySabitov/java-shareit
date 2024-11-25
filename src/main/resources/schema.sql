CREATE TABLE IF NOT EXISTS users (
user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(50) NOT NULL,
email varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(50) NOT NULL,
description varchar(500) NOT NULL,
owner_id BIGINT NOT NULL,
available BOOLEAN NOT NULL
);