#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

scp dp-server:/var/log/stocks-server/stocks.log \
        $STOCKS_ROOT/server/target/server.log
