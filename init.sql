-- Set the connection character set to UTF-8
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `db-dev`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `db-dev`;

CREATE TABLE IF NOT EXISTS `transaction_category` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `suggested_amount` double,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `badge` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `description` varchar(255),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `badge_tier` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `badge_id` bigint NOT NULL,
    `description` varchar(255),
    `tier` int,
    `target` int,
    PRIMARY KEY (`id`),
    FOREIGN KEY (badge_id) REFERENCES badge(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO badge (name, description)
VALUES ('Supersparings-gris', null),
       ('Teenage Mutant Ninja-piggy', null),
       ('Griseflex', null),
       ('Grisebank-er', null),
       ('Sus-pig-us', null),
       ('Griseglad', 'Logge inn for første gang'),
       ('Svinerik', 'Fullfør ditt første sparemål'),
       ('Griseflink sparer', 'Klar din første spareutfordring');


INSERT INTO badge_tier (badge_id, tier, target, description)
VALUES (1, 1, 1, 'Fullfør 1 sparemål '),
       (1, 2, 3, 'Fullfør 3 sparemål'),
       (1, 3, 10, 'Fullfør 10 sparemål'),
       (2, 1, 1, 'Fullfør 1 spareutfordring'),
       (2, 2, 10, 'Fullfør 10 spareutfordringer'),
       (2, 3, 50, 'Fullfør 50 spareutfordringer'),
       (3, 1, 4, 'Oppnå en streak på 4 uker'),
       (3, 2, 12, 'Oppnå en streak på 12 uker'),
       (3, 3, 52, 'Oppnå en streak på 52 uker'),
       (4, 1, 2000, 'Spar 2 000 kroner'),
       (4, 2, 10000, 'Spar 10 000 kroner'),
       (4, 3, 50000, 'Spar 50 000 kroner'),
       (5, 1, 1, 'Logg inn 1 gang'),
       (5, 2, 50, 'Logg inn 50 ganger'),
       (5, 3, 200, 'Logg inn 200 ganger');


INSERT INTO `transaction_category` (name, suggested_amount) VALUES
                                                               ('Dagligvarer', 1353),
                                                               ('Faste utgifter (bolig, forsikring, strøm)', 4249),
                                                               ('Klær, sko og tilbehør', 218),
                                                               ('Helse og personlig pleie', 185),
                                                               ('Uteliv', 464),
                                                               ('Transport', 749),
                                                               ('Fritid, underholdning', 863),
                                                               ('Hjem', 121),
                                                               ('Øvrig', 0);

