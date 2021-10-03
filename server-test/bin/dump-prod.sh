#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

BASEDIR="/home/jan/Programme/Java/stocks/server/src/main/resources/migrations"
DUMP="$STOCKS_ROOT/server-test/target/prod-dump.sql"
DUMP_FOR_SERVER="$STOCKS_ROOT/server-test/target/prod-dump-for-dp-server.sql"

if [[ ! -f $DUMP ]] ; then
    ssh -t db.j.njsm.de sudo -Eu postgres pg_dump -c stocks > "$DUMP"
    tail -n +2 "$DUMP" > tmp
    mv tmp "$DUMP"
    sed -i -E "s#[a-z:]*migrations#$BASEDIR#g" "$DUMP"
    sed -E "s#[a-z:]*migrations##g" < "$DUMP" > "$DUMP_FOR_SERVER"
fi

psql -U stocks -f "$DUMP"
