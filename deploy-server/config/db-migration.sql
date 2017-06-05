SET foreign_key_checks = 0;
    
DROP TABLE IF EXISTS EAN_number;

CREATE TABLE EAN_number (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `number` varchar(13) NOT NULL,
    `identifies` int UNSIGNED NOT NULL,
    PRIMARY KEY (`ID`),
    FOREIGN KEY (identifies) REFERENCES Food(`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Updates (`table_name`, `last_update`)
VALUES 
    ('EAN_number', NOW());
    

delimiter |    

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
