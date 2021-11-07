#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

$STOCKS_ROOT/server-test/bin/dump-prod.sh
ssh dp-server sudo -u postgres psql stocks < $STOCKS_ROOT/server-test/target/prod-dump-for-dp-server.sql
$STOCKS_ROOT/server-test/bin/initialise-new-device-after-dump.sh

