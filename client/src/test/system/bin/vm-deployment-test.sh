#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/tmp/
SERVER="dp-server"

DEVICE_ID=$(cat $STOCKS_ROOT/server-test/target/01_id)
TICKET_VALUE=$(cat $STOCKS_ROOT/server-test/target/01_ticket)

set -e

rm -rf $STOCKS_ROOT/client/target/client-server.log
rm -rf $STOCKS_ROOT/client/target/client-client.log

ansible-playbook $STOCKS_ROOT/deploy-client/install.yml

echo "##teamcity[testSuiteStarted name='Client System Test']"

echo "##teamcity[testStarted name='Initialisation']"
FINGERPRINT=$(curl -s http://dp-server:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')

echo -e "dp-server\n\n\n\nJack\ncli-client\n1\n$DEVICE_ID\n\
$FINGERPRINT\n\
$TICKET_VALUE\nquit\n" | \
        ssh dp-client stocks
echo "##teamcity[testFinished name='Initialisation']"

python $STOCKS_ROOT/client/src/test/system/bin/testcase-driver.py \
        `find $STOCKS_ROOT/client/src/test/system/usecases -type f | sort`

scp dp-server:/var/log/stocks-server/stocks.log \
        $STOCKS_ROOT/client/target/client-server.log
scp dp-client:\~/.stocks/stocks.log $STOCKS_ROOT/client/target/client-client.log

echo "##teamcity[testSuiteFinished name='Client System Test']"
echo

