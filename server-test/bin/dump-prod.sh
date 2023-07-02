#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

BASEDIR="/home/jan/Programme/Java/stocks/server/src/main/resources/migrations"
DUMP="$STOCKS_ROOT/server-test/target/prod-dump.sql"
DUMP_FOR_SERVER="$STOCKS_ROOT/server-test/target/prod-dump-for-dp-server.sql"
DEPLOYMENT_VM="${DEPLOYMENT_VM:-dp-server}"

if [[ ! -f $DUMP ]] ; then
    ssh -t eregion.m.njsm.de sudo -Eu postgres pg_dump -c stocks_customer_3 > "$DUMP"
    tail -n +2 "$DUMP" > tmp
    mv tmp "$DUMP"
    sed -E "s#[a-z:]*migrations/#migrations/#g" < "$DUMP" > "$DUMP_FOR_SERVER"
    sed -i -E "s#[a-z:]*migrations#$BASEDIR#g" "$DUMP"
fi

psql -U stocks -f "$DUMP"
