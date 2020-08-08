--liquibase formatted sql
-- stocks is client-server program to manage a household's food stock
-- Copyright (C) 2019  The stocks developers
--
-- This file is part of the stocks program suite.
--
-- stocks is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- stocks is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.

--changeset admin:1 context=production
CREATE TABLE "User"
(
    "ID"      SERIAL NOT NULL UNIQUE,
    "name"    TEXT   NOT NULL,
    "version" INT    NOT NULL DEFAULT 0,
    PRIMARY KEY ("ID")
);
--rollback drop table "User";

--changeset admin:2 context=production
CREATE TABLE "Location"
(
    "ID"      SERIAL NOT NULL UNIQUE,
    "name"    TEXT   NOT NULL,
    "version" INT    NOT NULL DEFAULT 0,
    PRIMARY KEY ("ID")
);
--rollback drop table "Location";

--changeset admin:3 context=production
CREATE TABLE "Food"
(
    "ID"       SERIAL  NOT NULL UNIQUE,
    "name"     TEXT    NOT NULL,
    "version"  INT     NOT NULL DEFAULT 0,
    "to_buy"   BOOLEAN NOT NULL DEFAULT false,
    "expiration_offset" INTERVAL DAY NOT NULL DEFAULT INTERVAL '0' SECOND,
    "location" INT              DEFAULT NULL,
    PRIMARY KEY ("ID"),
    FOREIGN KEY ("location") REFERENCES "Location" ("ID") ON UPDATE CASCADE ON DELETE SET NULL
);
--rollback drop table "Food";

--changeset admin:4 context=production
CREATE TABLE "User_device"
(
    "ID"       SERIAL NOT NULL UNIQUE,
    "name"     TEXT   NOT NULL,
    "version"  INT    NOT NULL DEFAULT 0,
    belongs_to INT    NOT NULL,
    CONSTRAINT "device_points_to_user" FOREIGN KEY ("belongs_to") REFERENCES "User" ("ID") ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY ("ID")
);
--rollback drop table "User_device";

--changeset admin:5 context=production
CREATE TABLE "Food_item"
(
    "ID"        SERIAL NOT NULL UNIQUE,
    "eat_by"    TIMESTAMP WITH TIME ZONE NOT NULL,
    "of_type"   INT    NOT NULL,
    "stored_in" INT    NOT NULL,
    "registers" INT    NOT NULL,
    "buys"      INT    NOT NULL,
    "version"   INT    NOT NULL DEFAULT 0,
    FOREIGN KEY (of_type) REFERENCES "Food" ("ID") ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stored_in) REFERENCES "Location" ("ID") ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (registers) REFERENCES "User_device" ("ID") ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (buys) REFERENCES "User" ("ID") ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY ("ID")
);
--rollback drop table "Food_item";

--changeset admin:6 context=production
CREATE TABLE "Ticket"
(
    "ID"             SERIAL    NOT NULL UNIQUE,
    "ticket"         TEXT      NOT NULL,
    "belongs_device" INT       NOT NULL,
    "created_on"     TIMESTAMP NOT NULL DEFAULT '2100-01-01 00:00:00.000',
    PRIMARY KEY ("ID"),
    FOREIGN KEY (belongs_device) REFERENCES "User_device" ("ID") ON DELETE CASCADE ON UPDATE CASCADE
);
--rollback drop table "Ticket";

--changeset admin:7 context=production
CREATE TABLE "Updates"
(
    "ID"          SERIAL NOT NULL,
    "table_name"  TEXT   NOT NULL,
    "last_update" TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY ("ID")
);
--rollback drop table "Updates";

--changeset admin:8 context=production
CREATE TABLE "EAN_number"
(
    "ID"         SERIAL NOT NULL,
    "number"     TEXT   NOT NULL,
    "identifies" INT    NOT NULL,
    "version"    INT    NOT NULL DEFAULT 0,
    PRIMARY KEY ("ID"),
    FOREIGN KEY (identifies) REFERENCES "Food" ("ID") ON DELETE CASCADE ON UPDATE CASCADE
);
--rollback drop table "EAN_number";

--changeset admin:9 context=production
INSERT INTO "Updates" ("table_name", "last_update")
VALUES ('Location', NOW()),
       ('User', NOW()),
       ('User_device', NOW()),
       ('Food', NOW()),
       ('Food_item', NOW()),
       ('EAN_number', NOW());
--rollback delete from "Updates";

--changeset admin:10 context=production
CREATE OR REPLACE FUNCTION update_timestamp() RETURNS TRIGGER
LANGUAGE PLPGSQL
AS '
BEGIN
UPDATE "Updates"
SET "last_update" = NOW()
WHERE "table_name" = TG_ARGV[0];
RETURN NEW;
END;
';
--rollback drop function update_timestamp;

--changeset admin:11 context=production
CREATE TRIGGER Location_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "Location"
    FOR EACH STATEMENT
EXECUTE PROCEDURE update_timestamp('Location');
--rollback drop trigger Location_timestamp_update on "Location";

--changeset admin:12 context=production
CREATE TRIGGER Food_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "Food"
    FOR EACH STATEMENT
EXECUTE PROCEDURE update_timestamp('Food');
--rollback drop trigger Food_timestamp_update on "Food";

--changeset admin:13 context=production
CREATE TRIGGER User_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "User"
    FOR EACH STATEMENT
EXECUTE PROCEDURE update_timestamp('User');
--rollback drop trigger User_timestamp_update on "User";

--changeset admin:14 context=production
CREATE TRIGGER User_device_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "User_device"
    FOR EACH STATEMENT
EXECUTE PROCEDURE update_timestamp('User_device');
--rollback drop trigger User_device_timestamp_update on "User_device";

--changeset admin:15 context=production
CREATE TRIGGER Food_item_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "Food_item"
    FOR EACH STATEMENT
EXECUTE PROCEDURE update_timestamp('Food_item');
--rollback drop trigger Food_item_timestamp_update on "Food_item";

--changeset admin:16 context=production
CREATE TRIGGER EAN_number_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "EAN_number"
    FOR EACH STATEMENT
EXECUTE PROCEDURE update_timestamp('EAN_number');
--rollback drop trigger EAN_number_timestamp_update on "EAN_number";
