#!/bin/sh

java -jar -Duser.stocks.dir=/home/$(whoami)/ /usr/lib/stocks/client.jar $@
