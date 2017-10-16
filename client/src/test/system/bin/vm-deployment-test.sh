#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

set -e

sudo virsh start dp-client
sudo virsh snapshot-revert dp-server initialised-running
sleep 10

ansible-playbook $STOCKS_ROOT/deploy-client/install.yml

echo "##teamcity[testSuiteStarted name='Client System Test']"

echo "##teamcity[testStarted name='Initialisation']"
FINGERPRINT=$(curl -s http://dp-server:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')

echo -e "dp-server\n\n\n\nJack\nDevice\n1\n1\n\
$FINGERPRINT\n\
0000\nquit\n" | \
        ssh dp-client stocks
echo "##teamcity[testFinished name='Initialisation']"

python $STOCKS_ROOT/client/src/test/system/bin/testcase-driver.py \
        `find $STOCKS_ROOT/client/src/test/system/usecases -type f | sort`

echo "##teamcity[testSuiteFinished name='Client System Test']"
echo

