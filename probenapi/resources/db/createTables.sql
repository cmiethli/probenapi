CREATE DATABASE IF NOT EXISTS `probenapi` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `probenapi`;

/* Drop Tables */
DROP TABLE IF EXISTS probe;

/* Create Tables */
CREATE TABLE probe(
    id bigint(20) NOT NULL,
    zeitpunkt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    messwert int,
    ergebnis varchar(40),
    PRIMARY KEY (id)
);
