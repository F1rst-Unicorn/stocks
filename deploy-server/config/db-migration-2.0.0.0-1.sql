SET foreign_key_checks = 0;

ALTER TABLE Food_item CHANGE COLUMN eat_by eat_by DATETIME(3) NOT NULL;
ALTER TABLE Updates CHANGE COLUMN last_update last_update DATETIME(3) NOT NULL;

UPDATE Updates SET last_update = UTC_TIMESTAMP(3);

DROP TRIGGER Location_insert;
DROP TRIGGER Location_update;
DROP TRIGGER Location_delete;
DROP TRIGGER User_insert;
DROP TRIGGER User_update;
DROP TRIGGER User_delete;
DROP TRIGGER User_device_insert;
DROP TRIGGER User_device_update;
DROP TRIGGER User_device_delete;
DROP TRIGGER Food_item_insert;
DROP TRIGGER Food_item_update;
DROP TRIGGER Food_item_delete;
DROP TRIGGER EAN_number_insert;
DROP TRIGGER EAN_number_update;
DROP TRIGGER EAN_number_delete;
DROP TRIGGER Food_insert;
DROP TRIGGER Food_update;
DROP TRIGGER Food_delete;


delimiter |

CREATE TRIGGER Location_insert AFTER INSERT ON `Location` FOR EACH ROW
  BEGIN
    UPDATE Updates
    SET last_update=UTC_TIMESTAMP(3)
    WHERE `table_name`='Location';
  END;

CREATE TRIGGER Location_update
AFTER UPDATE ON Location FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Location';
  END;

CREATE TRIGGER Location_delete
AFTER DELETE ON Location FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Location';
  END;



CREATE TRIGGER Food_insert
AFTER INSERT ON Food FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Food';
  END;

CREATE TRIGGER Food_update
AFTER UPDATE ON Food FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Food';
  END;

CREATE TRIGGER Food_delete
AFTER DELETE ON Food FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Food';
  END;



CREATE TRIGGER User_insert
AFTER INSERT ON `User` FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='User';
  END;

CREATE TRIGGER User_update
AFTER UPDATE ON `User` FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='User';
  END;

CREATE TRIGGER User_delete
AFTER DELETE ON `User` FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='User';
  END;



CREATE TRIGGER User_device_insert
AFTER INSERT ON User_device FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='User_device';
  END;

CREATE TRIGGER User_device_update
AFTER UPDATE ON User_device FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='User_device';
  END;

CREATE TRIGGER User_device_delete
AFTER DELETE ON User_device FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='User_device';
  END;



CREATE TRIGGER Food_item_insert
AFTER INSERT ON Food_item FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Food_item';
  END;

CREATE TRIGGER Food_item_update
AFTER UPDATE ON Food_item FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Food_item';
  END;

CREATE TRIGGER Food_item_delete
AFTER DELETE ON Food_item FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='Food_item';
  END;



CREATE TRIGGER EAN_number_insert
AFTER INSERT ON EAN_number FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='EAN_number';
  END;

CREATE TRIGGER EAN_number_update
AFTER UPDATE ON EAN_number FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='EAN_number';
  END;

CREATE TRIGGER EAN_number_delete
AFTER DELETE ON EAN_number FOR EACH ROW
  BEGIN
    UPDATE Updates SET last_update=UTC_TIMESTAMP(3) WHERE `table_name`='EAN_number';
  END;

|

delimiter ;

SET foreign_key_checks = 1;
