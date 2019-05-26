#!/bin/bash

# stocks is client-server program to manage a household's food stock
# Copyright (C) 2019  The stocks developers
#
# This file is part of the stocks program suite.
#
# stocks is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# stocks is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
    mv $file $STOCKS_ROOT/client/src/test/system/usecases/$NEWNAME
done
