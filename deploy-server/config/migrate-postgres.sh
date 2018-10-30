#!/bin/sh

java -jar -Dde.njsm.stocks.postgresqlmigration.configPath=/etc/stocks-server/stocks.properties \
        /usr/lib/stocks-server/migrate-postgres.jar $@
