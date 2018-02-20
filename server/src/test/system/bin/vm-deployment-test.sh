#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

rm -rf $STOCKS_ROOT/server/target/server.log

set -e

echo "##teamcity[testSuiteStarted name='Server System Test']"

echo "##teamcity[testStarted name='Server installation']"
ansible-playbook -b -i $STOCKS_ROOT/deploy-server/inventory-testing \
        --extra-vars "ansible_become_pass=,ansible_sudo_pass= "     \
        $STOCKS_ROOT/deploy-server/play_install.yml
echo "##teamcity[testFinished name='Server installation']"

$STOCKS_ROOT/server/src/test/system/bin/fresh-installation-test.sh dp-server

scp dp-server:/var/log/stocks-server/stocks.log \
        $STOCKS_ROOT/server/target/server.log

echo "##teamcity[testSuiteFinished name='Server System Test']"

