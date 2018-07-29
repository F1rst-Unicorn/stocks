#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

rm -rf $STOCKS_ROOT/server/target/server.log

echo "##teamcity[testSuiteStarted name='Server System Test']"

echo "##teamcity[testStarted name='Server installation']"
ansible-playbook -b -i $STOCKS_ROOT/deploy-server/inventory-testing \
        --extra-vars "ansible_become_pass=,ansible_sudo_pass= "     \
        $STOCKS_ROOT/deploy-server/play_install.yml
echo "##teamcity[testFinished name='Server installation']"
echo "##teamcity[testSuiteFinished name='Server System Test']"

mvn -f $STOCKS_ROOT/server-test/pom.xml -Dtest=TestSuite test
