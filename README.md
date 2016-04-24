# Stocks

Stocks is a framework for helping people to keep track of their
food stocks, periodic tasks and tasks in daily life. 

# Installation

## Database

Stocks needs a MariaDB database. Set it up and make it reachable over localhost. 
Then set the credentials in root/stocks.properties to make it accessible to
the server. Import the schema in database/schema.sql into the database. 

## CA

Stocks drives its own CA. Set it up in root/CA/ using the already existing 
config files. Adjust the paths inside!. Note that the CA is only needed to 
certify the intermediate CA which is used for daily business. Copy the CA's 
certificate into root/nginx/ca.

## OCSP server

Stocks needs an OCSP server. Use openssl to set it up and make it listen on 
port $ocspPort (default 10920). Define that port in root/CA/intermediate/openssl.cnf, too 
(in the OCSP section). Also set the port in root/nginx/stocks/nginx.conf. 

## Jetty

Get a recent version of Jetty 9. Optionally adjust the ports in 
root/stocks.properties and update it in /root/nginx/stocks/nginx.conf. 
