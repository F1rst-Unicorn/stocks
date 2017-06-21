#!/bin/sh

java -jar -Duser.stocks.dir=/home/$(whoami)/ \
    -Dsystem.stocks.dir=/usr/share/stocks/   \
    /usr/lib/stocks/client.jar $@
