#!/bin/bash

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

sudo virsh start dp-server
sleep 10

echo "##teamcity[testSuiteStarted name='Server System Test']"

echo "##teamcity[testStarted name='Server installation']"
ansible-playbook $STOCKS_ROOT/deploy-server/install.yml
echo "##teamcity[testFinished name='Server installation']"


echo "##teamcity[testStarted name='Server deployment']"
ansible-playbook $STOCKS_ROOT/deploy-server/deploy.yml
echo "##teamcity[testFinished name='Server deployment']"

sleep 15

sudo virsh snapshot-delete dp-server initialised-running || true
sudo virsh snapshot-create-as dp-server --name initialised-running

$STOCKS_ROOT/server/src/test/system/bin/fresh-installation-test.sh dp-server

echo "##teamcity[testSuiteFinished name='Server System Test']"

