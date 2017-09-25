SET foreign_key_checks = 0;

ALTER TABLE Food_item CHANGE COLUMN eat_by eat_by DATETIME(3) NOT NULL;
ALTER TABLE Updates CHANGE COLUMN last_update last_update DATETIME(3) NOT NULL;

SET foreign_key_checks = 1;
