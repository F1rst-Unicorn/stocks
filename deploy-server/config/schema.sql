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

DROP TABLE IF EXISTS "Food" CASCADE;

CREATE TABLE "Food" (
  "ID" SERIAL NOT NULL UNIQUE,
  "name" TEXT NOT NULL,
  "version" INT NOT NULL DEFAULT 0,
  "to_buy" BOOLEAN NOT NULL DEFAULT false,
  PRIMARY KEY ("ID")
);

DROP TABLE IF EXISTS "User" CASCADE;

CREATE TABLE "User" (
    "ID" SERIAL NOT NULL UNIQUE,
    "name" TEXT NOT NULL,
    "version" INT NOT NULL DEFAULT 0,
    PRIMARY KEY ("ID")
);

DROP TABLE IF EXISTS "Location" CASCADE;

CREATE TABLE "Location" (
    "ID" SERIAL NOT NULL UNIQUE,
    "name" TEXT NOT NULL,
    "version" INT NOT NULL DEFAULT 0,
    PRIMARY KEY ("ID")
);

DROP TABLE IF EXISTS "User_device" CASCADE;

CREATE TABLE "User_device" (
    "ID" SERIAL NOT NULL UNIQUE,
    "name" TEXT NOT NULL,
    "version" INT NOT NULL DEFAULT 0,
    belongs_to INT NOT NULL,
    CONSTRAINT "device_points_to_user" FOREIGN KEY ("belongs_to") REFERENCES "User"("ID") ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY ("ID")
);

DROP TABLE IF EXISTS "Food_item" CASCADE;

CREATE TABLE "Food_item" (
    "ID" SERIAL NOT NULL UNIQUE,
    "eat_by" TIMESTAMP WITH TIME ZONE NOT NULL,
    "of_type" INT NOT NULL,
    "stored_in" INT NOT NULL,
    "registers" INT NOT NULL,
    "buys" INT NOT NULL,
    "version" INT NOT NULL DEFAULT 0,
    FOREIGN KEY (of_type) REFERENCES "Food"("ID") ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stored_in) REFERENCES "Location"("ID") ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (registers) REFERENCES "User_device"("ID") ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (buys) REFERENCES "User"("ID") ON DELETE RESTRICT ON UPDATE CASCADE,
    PRIMARY KEY ("ID")
);

DROP TABLE IF EXISTS "Ticket" CASCADE;

CREATE TABLE "Ticket" (
    "ID" SERIAL NOT NULL UNIQUE,
    "ticket" TEXT NOT NULL,
    "belongs_device" INT NOT NULL,
    "created_on" TIMESTAMP NOT NULL DEFAULT '2100-01-01 00:00:00.000',
    PRIMARY KEY ("ID"),
    FOREIGN KEY (belongs_device) REFERENCES "User_device"("ID") ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS "Updates" CASCADE;

CREATE TABLE "Updates" (
    "ID" SERIAL NOT NULL,
    "table_name" TEXT NOT NULL,
    "last_update" TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY ("ID")
);

DROP TABLE IF EXISTS "EAN_number" CASCADE;

CREATE TABLE "EAN_number" (
    "ID" SERIAL NOT NULL,
    "number" TEXT NOT NULL,
    "identifies" INT NOT NULL,
    "version" INT NOT NULL DEFAULT 0,
    PRIMARY KEY ("ID"),
    FOREIGN KEY (identifies) REFERENCES "Food"("ID") ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO "Updates" ("table_name", "last_update")
VALUES
       ('Location', NOW()),
       ('User', NOW()),
       ('User_device', NOW()),
       ('Food', NOW()),
       ('Food_item', NOW()),
       ('EAN_number', NOW());

DROP FUNCTION IF EXISTS "update_timestamp" CASCADE;

CREATE FUNCTION update_timestamp()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    AS $$
        BEGIN
            UPDATE "Updates"
            SET "last_update" = NOW()
            WHERE "table_name" = TG_ARGV[0];

            RETURN NEW;
        END;
    $$;

CREATE TRIGGER Location_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "Location"
    FOR EACH STATEMENT
    EXECUTE PROCEDURE update_timestamp('Location');

CREATE TRIGGER Food_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "Food"
    FOR EACH STATEMENT
    EXECUTE PROCEDURE update_timestamp('Food');

CREATE TRIGGER User_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "User"
    FOR EACH STATEMENT
    EXECUTE PROCEDURE update_timestamp('User');

CREATE TRIGGER User_device_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "User_device"
    FOR EACH STATEMENT
    EXECUTE PROCEDURE update_timestamp('User_device');

CREATE TRIGGER Food_item_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "Food_item"
    FOR EACH STATEMENT
    EXECUTE PROCEDURE update_timestamp('Food_item');

CREATE TRIGGER EAN_number_timestamp_update
    AFTER INSERT OR UPDATE OR DELETE ON "EAN_number"
    FOR EACH STATEMENT
    EXECUTE PROCEDURE update_timestamp('EAN_number');
