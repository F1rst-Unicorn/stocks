#!/bin/bash

if [[ $# -ne 1 ]] ; then
    echo "Usage: $0 <usecase number>"
    exit 1
fi

if ! echo $1 | egrep "[0-9][0-9][0-9][0-9]" >/dev/null ; then
    echo "Use case has exactly four digits"
    exit 1
fi

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

CURRENT=$(($1 + 1 - 1000))
for file in `find $STOCKS_ROOT/client/src/test/system/usecases -type f | \
        sort | tail -n +$CURRENT | sort -r` ; do
    NEWNAME=`echo $file | sed -r 's$.*/([0-9]*)\.txt$\1$g'`
    NEWNAME=$(($NEWNAME + 1))
    NEWNAME=$NEWNAME.txt
    echo mv $file $STOCKS_ROOT/client/src/test/system/usecases/$NEWNAME
done