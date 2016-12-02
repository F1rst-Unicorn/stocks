SET foreign_key_checks = 0;
    
DROP TABLE IF EXISTS EAN_Number;

CREATE TABLE EAN_Number (
    `ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `number` varchar(13) NOT NULL,
    `identifies` int UNSIGNED NOT NULL,
    PRIMARY KEY (`ID`),
    FOREIGN KEY (identifies) REFERENCES Food(`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Updates (`table_name`, `last_update`)
VALUES 
    ('EAN_Number', NOW());
    

delimiter |    

CREATE TRIGGER EAN_Number_insert
AFTER INSERT ON EAN_Number FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='EAN_Number';
END;

CREATE TRIGGER EAN_Number_update
AFTER UPDATE ON EAN_Number FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='EAN_Number';
END;

CREATE TRIGGER EAN_Number_delete
AFTER DELETE ON EAN_Number FOR EACH ROW
BEGIN
    UPDATE Updates SET last_update=NOW() WHERE `table_name`='EAN_Number';
END;

|

delimiter ;

SET foreign_key_checks = 1;
