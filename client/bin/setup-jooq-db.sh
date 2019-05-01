#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../"
SCHEMA=$1
DB=$2

mkdir -p $(dirname $DB)
rm -rf $DB
cat $SCHEMA | sqlite3 $DB
