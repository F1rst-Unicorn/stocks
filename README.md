# Stocks

Stocks is a framework for helping people to keep track of their
food stocks, periodic tasks and tasks in daily life. 

# Server Installation

## Database

Stocks needs a MariaDB database. Set it up and make it reachable over 
localhost. Create a user for the server. Then set the credentials in 
/etc/stocks/stocks.properties to make it accessible to the server. 
Import the schema in /usr/share/stocks/schema.sql into the database. 

## CA

Run the script in /usr/lib/stocks/setup-ca as user stocks to generate a 
CA and all needed keys in /usr/share/stocks/root/CA. 

## nginx

Set up nginx as reverse proxy with the corresponding SSL configs. 
For an example config have a look at /usr/share/stocks/nginx.conf.
Give nginx access to the certificates generated above. Particularly

* server.key.pem
* server.cert.pem
* ca-chain.cert.pem
* ca.cert.pem

## OCSP server

Stocks needs an OCSP server. If you adhere to the default config in 
the CA section, you can simply run the stocks-ocsp service. 

## Start up the components

* Start mysql
* Start OCSP
* Start nginx
* Start jetty

## Adding the first user

Due to the protocol to add new users, adding the first one has to be done
manually. Log into the database and add a user, user device and ticket. E.g. 

INSERT INTO User (name) VALUES (desired_name);
INSERT INTO User_device (name, belongs_to) VALUES (desired_name, LAST_INSERT_ID());
INSERT INTO Ticket (ticket, belongs_device) VALUES (some_string, LAST_INSERT_ID());

Then you can read out the needed values from the database and talk to the 
sentry (see spec/new-user-creation-protocol.md). 
