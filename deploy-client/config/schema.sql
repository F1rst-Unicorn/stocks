DROP TABLE IF EXISTS `Food`;

CREATE TABLE `Food` (
  `ID` INT NOT NULL UNIQUE,
  `name` varchar(200) NOT NULL,
  `image_path` varchar(200) NOT NULL DEFAULT '',
  `version` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`ID`)
);



DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
    `ID` INT NOT NULL UNIQUE,
    `name` varchar(200) NOT NULL,
    `version` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`ID`)
);



DROP TABLE IF EXISTS `Location`;

CREATE TABLE `Location` (
    `ID` INT NOT NULL UNIQUE,
    `name` varchar(200) NOT NULL,
    `version` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`ID`)
);


DROP TABLE IF EXISTS User_device;

CREATE TABLE User_device (
    `ID` INT NOT NULL UNIQUE,
    `name` varchar(200) NOT NULL,
    belongs_to INT NOT NULL,
    `version` INT NOT NULL DEFAULT 0,
    CONSTRAINT `device_poINTs_to_user` FOREIGN KEY (`belongs_to`) REFERENCES `User`(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (`ID`)
);



DROP TABLE IF EXISTS Food_item;

CREATE TABLE Food_item (
    `ID` INT NOT NULL UNIQUE,
    `eat_by` DATETIME NOT NULL,
    `of_type` INT NOT NULL,
    `stored_in` INT NOT NULL,
    `registers` INT NOT NULL,
    `buys` INT NOT NULL,
    `version` INT NOT NULL DEFAULT 0,
    FOREIGN KEY (of_type) REFERENCES Food(`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stored_in) REFERENCES Location(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (registers) REFERENCES User_device(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (buys) REFERENCES `User`(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (`ID`)
);


DROP TABLE IF EXISTS Updates;

CREATE TABLE Updates (
    `ID` INT NOT NULL,
    `table_name` varchar(200) NOT NULL,
    `last_update` DATETIME NOT NULL DEFAULT 0,
    PRIMARY KEY (`ID`)
);

INSERT INTO Updates (`ID`, `table_name`)
VALUES
    (1, 'Location'),
    (2, 'User'),
    (3, 'User_device'),
    (4, 'Food'),
    (5, 'Food_item');

DROP TABLE IF EXISTS Config;

CREATE TABLE Config (
    `key` varchar(100) NOT NULL UNIQUE,
    `value` varchar(100) NOT NULL,
    PRIMARY KEY (`key`)
);

INSERT INTO Config (`key`, `value`)
VALUES
    ('db.version', '3.0.0')
