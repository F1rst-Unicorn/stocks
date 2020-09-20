-- You have to create an initial user for your database.
INSERT INTO "User" (name) VALUES ('<username>');
INSERT INTO "User_device" (name, belongs_to) VALUES ('<device>', LASTVAL());
INSERT INTO "Ticket" (ticket, belongs_device) VALUES ('0000', LASTVAL())
