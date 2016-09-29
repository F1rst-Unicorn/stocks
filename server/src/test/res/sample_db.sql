-- Fill the database with sample data

SET FOREIGN_KEY_CHECKS = 0;

-- Wipe any present data
DELETE FROM Food;
DELETE FROM Food_item;
DELETE FROM User;
DELETE FROM User_device;
DELETE FROM Ticket;
DELETE FROM Location;


INSERT INTO Food (ID, name) VALUES (1, 'Carrot'), (2, 'Beer'), (3, 'Cheese');

INSERT INTO Location (ID, name) VALUES (1, 'Fridge') , (2, 'Cupboard');

INSERT INTO User (ID, name) VALUES (1, 'Bob'), (2, 'Alice');

INSERT INTO User_device (ID, name, belongs_to) VALUES (1, 'mobile', 1), (2, 'laptop', 2), (3, 'pending_device', 2);

INSERT INTO Food_item (ID, eat_by, registers, buys, stored_in, of_type) VALUES
  -- Bob bought three bottles of beer for the fridge
  (1, '1-1-2017', 1, 1, 1, 2),
  (2, '1-1-2017', 1, 1, 1, 2),
  (3, '1-1-2017', 1, 1, 1, 2);

INSERT INTO Ticket (ticket, belongs_device) VALUES ('AAAA', 3);

SET FOREIGN_KEY_CHECKS = 1;