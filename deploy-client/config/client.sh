#!/bin/sh

java -jar -Duser.stocks.dir=/home/$(whoami)/ \
    -Dsystem.stocks.dir=/usr/share/stocks/   \
    -Dorg.jooq.no-logo=true                  \
    /usr/lib/stocks/client.jar $@
