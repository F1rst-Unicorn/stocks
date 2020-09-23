# Upgrading the server

It is only possible to upgrade the server version to the next each. For a
successful upgrade just follow the instructions listed here in the order they
are listed, from your current version, to the version you want to upgrade to.

If a version is not listed here this means upgrading the distribution's package
suffices.

If the instruction is to simply perform an upgrade via the distribution's
package manager this means that the tomcat container has to be restarted after
installation.

## 5.2.0.0

Stocks can now run as several instances on one host. Some changes to the file
system have to be made to adapt to this. This guide assumes your current
instance is called "stocks".

The CA and nginx download point which used to be stored at
`/usr/share/stocks-server/root` are  now to be stored at
`/usr/share/stocks-server/instances/stocks`. Change the directory accordingly.

The key material in `/etc/nginx/ssl` is now stored differently. The first
mention is the name of the application while the second mention reflects the
name of the instance.

* `stocks.cert.pem` -> `stocks-stocks.cert.pem`
* `stocks.key.pem` -> `stocks-stocks.key.pem`
* `stocks.ca-chain.pem` -> `stocks-stocks.ca-chain.pem`

## 4.8.0.0-1

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. Finally start up tomcat again.
This will be the last time to migrate the database manually.

## 4.5.0.0-1

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. Finally start up tomcat again.

## 4.4.0.0-1

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. Finally start up tomcat again.

## 4.3.0.0-1

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. Finally start up tomcat again.

## 4.1.0.0-1

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. Finally start up tomcat again.

## 4.0.0.0-1

A new config option was added. Perform the following steps:

* Perform the pacman upgrade
* Merge the configuration with `pacdiff`
* Restart the tomcat application server

Note that clients will have to upgrade to the latest version according to the
compatibility matrix.

## 3.0.0.0-1

This version switches from MariaDB to PostgreSQL. To migrate your data perform
the following steps:

* Stop the tomcat application server
* Perform the pacman upgrade
* Setup an empty PostgreSQL database for stocks
* Import /usr/share/stocks-server/schema.sql
* Edit /etc/stocks-server/stocks.properties and add the new configuration
  options for your database instance.
* Run /usr/lib/stocks-server/migrate-to-postgres
* Start the tomcat application server

## 2.1.0.0-1

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. Finally start up tomcat again.

## 2.0.0.0-0

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. This changes the date format of
several columns

## 0.5.7

Changes in the database model require a database migration. Shut down the tomcat
server and do the pacman upgrade. Then execute the SQL script provided in
/usr/share/stocks-server/db-migration.sql. This adds new tables and triggers to
the database. Once this is complete, start the new server.

## 0.5.6

It is recommended to do a full server migration as described in maintenance.md
section "Stocks migration". Reason for this are the changes in the container:
Before 5.6.0 there was a Jetty container, which is exchanged by tomcat8.

## Before 0.5

No instructions are provided. Best do a full migration as described in
maintenance.md section "Stocks migration" or contact the developers for

## License

Copyright (C)  2019  The stocks developers

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".

individual support.
