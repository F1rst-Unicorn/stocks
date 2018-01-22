SET foreign_key_checks = 0;

ALTER TABLE Food_item ADD COLUMN `version` int UNSIGNED NOT NULL;
ALTER TABLE Food ADD COLUMN `version` int UNSIGNED NOT NULL;
ALTER TABLE Location ADD COLUMN `version` int UNSIGNED NOT NULL;
ALTER TABLE User ADD COLUMN `version` int UNSIGNED NOT NULL;
ALTER TABLE User_device ADD COLUMN `version` int UNSIGNED NOT NULL;
ALTER TABLE EAN_number ADD COLUMN `version` int UNSIGNED NOT NULL;

SET foreign_key_checks = 1;
