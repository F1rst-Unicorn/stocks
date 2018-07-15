DROP TABLE IF EXISTS `Food`;

CREATE TABLE `Food` (
  `ID` int UNSIGNED NOT NULL UNIQUE,
  `name` varchar(200) NOT NULL,
  `image_path` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
);



DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
    `ID` int UNSIGNED NOT NULL UNIQUE,
    `name` varchar(200) NOT NULL,
    PRIMARY KEY (`ID`)
);



DROP TABLE IF EXISTS `Location`;

CREATE TABLE `Location` (
    `ID` int UNSIGNED NOT NULL UNIQUE,
    `name` varchar(200) NOT NULL,
    PRIMARY KEY (`ID`)
);


DROP TABLE IF EXISTS User_device;

CREATE TABLE User_device (
    `ID` int UNSIGNED NOT NULL UNIQUE,
    `name` varchar(200) NOT NULL,
    belongs_to int UNSIGNED NOT NULL,
    CONSTRAINT `device_points_to_user` FOREIGN KEY (`belongs_to`) REFERENCES `User`(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (`ID`)
);



DROP TABLE IF EXISTS Food_item;

CREATE TABLE Food_item (
    `ID` int UNSIGNED NOT NULL UNIQUE,
    `eat_by` DATETIME NOT NULL,
    `of_type` int UNSIGNED NOT NULL,
    `stored_in` int UNSIGNED NOT NULL,
    `registers` int UNSIGNED NOT NULL,
    `buys` int UNSIGNED NOT NULL,    
    FOREIGN KEY (of_type) REFERENCES Food(`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stored_in) REFERENCES Location(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (registers) REFERENCES User_device(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (buys) REFERENCES `User`(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (`ID`)
);

    
DROP TABLE IF EXISTS Updates;

CREATE TABLE Updates (
    `ID` int UNSIGNED NOT NULL,
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
    ('db.version', '2.0.2')
