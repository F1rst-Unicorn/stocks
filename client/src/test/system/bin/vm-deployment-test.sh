#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

set -e

if [[ -z $CI_SERVER ]] ; then
    SERVER=dp-client-server
else
    SERVER=dp-server
fi

# virsh reset
sudo virsh snapshot-revert $SERVER initialised-running
sleep 1

rm -rf $STOCKS_ROOT/client/src/test/system/tmp
mkdir -p $STOCKS_ROOT/client/src/test/system/tmp/.stocks
echo ".read $STOCKS_ROOT/deploy-client/config/schema.sql" | \
        sqlite3 $STOCKS_ROOT/client/src/test/system/tmp/.stocks/stocks.db

FINGERPRINT=$(curl -s http://$SERVER:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')

echo -e "$SERVER\n\n\n\nJack\nDevice\n1\n1\n\
$FINGERPRINT\n\
0000\nrefresh\nuser\ndev\nfood\nloc\nquit\n" \
        | java -jar -Duser.stocks.dir=$STOCKS_ROOT/client/src/test/system/tmp \
        -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9999 \
        $STOCKS_ROOT/client/target/client-*.jar

rm -rf $STOCKS_ROOT/client/src/test/system/tmp
sudo virsh snapshot-revert $SERVER clean

echo "##teamcity[jacocoReport dataPath='/tmp/jacoco-client.exec' includes='de.njsm.stocks.*']"
