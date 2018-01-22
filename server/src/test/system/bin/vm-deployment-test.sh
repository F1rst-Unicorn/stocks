#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

sudo virsh start dp-server && sleep 10

set -e

echo "##teamcity[testSuiteStarted name='Server System Test']"

echo "##teamcity[testStarted name='Server installation']"
ansible-playbook -b -i $STOCKS_ROOT/deploy-server/inventory-testing \
        --extra-vars "ansible_become_pass=,ansible_sudo_pass= "     \
        $STOCKS_ROOT/deploy-server/play_install.yml
echo "##teamcity[testFinished name='Server installation']"

sleep 15

sudo virsh snapshot-delete dp-server initialised-running || true
sudo virsh snapshot-create-as dp-server --name initialised-running

$STOCKS_ROOT/server/src/test/system/bin/fresh-installation-test.sh dp-server

echo "##teamcity[testSuiteFinished name='Server System Test']"

