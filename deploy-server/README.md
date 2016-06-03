# Server Installation

## Database

Stocks needs a MariaDB database. Set it up and make it reachable over 
localhost. Create a user for the server. Then set the credentials in 
/etc/stocks-server/stocks.properties to make it accessible to the server. 
Import the schema in /usr/share/stocks-server/schema.sql into the database. 

## CA

Run the script in /usr/lib/stocks-server/setup-ca as user stocks to generate a 
CA and all needed keys in /usr/share/stocks-server/root/CA. 

## nginx

Set up nginx as reverse proxy with the corresponding SSL configs. 
For an example config have a look at /usr/share/stocks-server/nginx.conf.
Give nginx access to the certificates generated above. Particularly

* server.key.pem
* server.cert.pem
* ca-chain.cert.pem
* ca.cert.pem

Furthermore the stocks server must be able to reload nginx to update the CRL
nginx reads from. This means the user stocks must be allowed to execute 
/usr/lib/stocks-server/nginx-reload as root via sudo. Add the following line
to your /etc/sudoers file

stocks ALL=NOPASSWD: /usr/lib/stocks-server/nginx-reload

## Jetty server

To adapt the stocks server to your system, adjust the needed values in 
/etc/stocks-server/stocks.properties. 

## Start up the components

* Start mysql
* Start jetty
* Start nginx

## Adding the first user

Due to the protocol to add new users, adding the first one has to be done
manually. Log into the database and add a user, user device and ticket. E.g. 

INSERT INTO User (name) VALUES (desired_name);
INSERT INTO User_device (name, belongs_to) VALUES (desired_name, LAST_INSERT_ID());
INSERT INTO Ticket (ticket, belongs_device) VALUES (some_string, LAST_INSERT_ID());

Then you can read out the needed values from the database and talk to the 
sentry (see spec/new-user-creation-protocol.md). 
 
