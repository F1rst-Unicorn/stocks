# Stocks

Stocks is a framework for helping people to keep track of their
food stocks, periodic tasks and tasks in daily life. 

# Installation

## Database

Stocks needs a MariaDB database. Set it up and make it reachable over localhost. 
Then set the credentials in /etc/stocks/stocks.properties to make it accessible to
the server. Import the schema in /usr/share/stocks/schema.sql into the database. 

## CA

Run the script in /usr/lib/stocks/setup-ca as user stocks to generate a CA and all needed keys in
/usr/share/stocks/root/CA. 

## nginx

Set up nginx as reverse proxy with the corresponding SSL configs. For an example config
have a look at /usr/share/stocks/nginx.conf

## OCSP server

Stocks needs an OCSP server. If you adhere to the default config in the CA section, 
you can simply run the stocks-ocsp service. 

## Start up the components

* Start mysql
* Start OCSP
* Start nginx
* Start jetty
