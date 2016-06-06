# Client installation

## SQLite database

Create a skeleton database by either using the database from 
/usr/share/stocks/proto.db or creating a new database and
importing schema.sql into it. Place the database in ~/.stocks/stocks.db. 

## First startup

Now you can start the client. It will first ask for some server config values.
For a vanilla server there is no need for adjustments, just press enter
to apply the defaults. Then a new certificate is generated using the values 
provided either by an existing user or using the created principals for the 
first user (see deploy-server/README.md). 
After that you can do a refresh to get the recest copy of the stocks. 
