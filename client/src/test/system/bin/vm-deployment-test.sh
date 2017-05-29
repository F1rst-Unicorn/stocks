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

echo
echo "##teamcity[testSuiteStarted name='Client System Test']"

echo "##teamcity[testStarted name='Initialisation']"
FINGERPRINT=$(curl -s http://$SERVER:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')

echo -e "$SERVER\n\n\n\nJack\nDevice\n1\n1\n\
$FINGERPRINT\n\
0000\nquit\n" | \
        java -jar -Duser.stocks.dir=$STOCKS_ROOT/client/src/test/system/tmp \
        $STOCKS_ROOT/client/target/client-*.jar
echo "##teamcity[testFinished name='Initialisation']"

python $STOCKS_ROOT/client/src/test/system/bin/testcase-driver.py \
        `find $STOCKS_ROOT/client/src/test/system/usecases -type f | sort`

echo "##teamcity[testSuiteFinished name='Client System Test']"
echo

sudo virsh snapshot-revert $SERVER clean