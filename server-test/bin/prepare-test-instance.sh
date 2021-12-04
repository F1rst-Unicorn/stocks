#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

DEPLOYMENT_VM="${DEPLOYMENT_VM:-dp-server}"

$STOCKS_ROOT/server-test/bin/dump-prod.sh
ssh $DEPLOYMENT_VM sudo -u postgres psql stocks < $STOCKS_ROOT/server-test/target/prod-dump-for-dp-server.sql
$STOCKS_ROOT/server-test/bin/initialise-new-device-after-dump.sh

