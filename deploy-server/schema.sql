SET foreign_key_checks = 0;

DROP TABLE IF EXISTS `Food`;

CREATE TABLE `Food` (
  `ID` int UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    `name` varchar(200) NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `Location`;

CREATE TABLE `Location` (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    `name` varchar(200) NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS User_device;

CREATE TABLE User_device (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    `name` varchar(200) NOT NULL,
    belongs_to int UNSIGNED NOT NULL,
    CONSTRAINT `device_points_to_user` FOREIGN KEY (`belongs_to`) REFERENCES `User`(`ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS Food_item;

CREATE TABLE Food_item (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS Ticket;

CREATE TABLE Ticket (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    `ticket` varchar(64) NOT NULL,
    `belongs_device` int UNSIGNED NOT NULL,
    `created_on` DATETIME NOT NULL DEFAULT '2100-01-01 00:00:00',
    PRIMARY KEY (`ID`),
    FOREIGN KEY (belongs_device) REFERENCES User_device(`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;    

    
DROP TABLE IF EXISTS Updates;

CREATE TABLE Updates (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `table_name` varchar(200) NOT NULL,
    `last_update` DATETIME NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Updates (`table_name`, `last_update`)
VALUES 
    ('Location', NOW()),
    ('User', NOW()),
    ('User_device', NOW()),
    ('Food', NOW()),
    ('Food_item', NOW());
    ('EAN_number', NOW());
    
DROP TABLE IF EXISTS EAN_number;

CREATE TABLE EAN_number (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `number` varchar(13) NOT NULL,
    `identifies` int UNSIGNED NOT NULL,
    PRIMARY KEY (`ID`),
    FOREIGN KEY (identifies) REFERENCES Food(`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    

delimiter |    

CREATE TRIGGER Location_insert AFTER INSERT ON `Location` FOR EACH ROW
BEGIN
    UPDATE Updates 
    SET last_update=NOW() 
    WHERE `table_name`='Location';
END;

CREATE TRIGGER Location_update
AFTER UPDATE ON Location FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Location';
END;

CREATE TRIGGER Location_delete
AFTER DELETE ON Location FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Location';
END;



CREATE TRIGGER Food_insert
AFTER INSERT ON Food FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Food';
END;

CREATE TRIGGER Food_update
AFTER UPDATE ON Food FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Food';
END;

CREATE TRIGGER Food_delete
AFTER DELETE ON Food FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Food';
END;



CREATE TRIGGER User_insert
AFTER INSERT ON `User` FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='User';
END;

CREATE TRIGGER User_update
AFTER UPDATE ON `User` FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='User';
END;

CREATE TRIGGER User_delete
AFTER DELETE ON `User` FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='User';
END;



CREATE TRIGGER User_device_insert
AFTER INSERT ON User_device FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='User_device';
END;

CREATE TRIGGER User_device_update
AFTER UPDATE ON User_device FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='User_device';
END;

CREATE TRIGGER User_device_delete
AFTER DELETE ON User_device FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='User_device';
END;



CREATE TRIGGER Food_item_insert
AFTER INSERT ON Food_item FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Food_item';
END;

CREATE TRIGGER Food_item_update
AFTER UPDATE ON Food_item FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Food_item';
END;

CREATE TRIGGER Food_item_delete
AFTER DELETE ON Food_item FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='Food_item';
END;



CREATE TRIGGER EAN_number_insert
AFTER INSERT ON EAN_number FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='EAN_number';
END;

CREATE TRIGGER EAN_number_update
AFTER UPDATE ON EAN_number FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='EAN_number';
END;

CREATE TRIGGER EAN_number_delete
AFTER DELETE ON EAN_number FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='EAN_number';
END;

|

delimiter ;

SET foreign_key_checks = 1;
