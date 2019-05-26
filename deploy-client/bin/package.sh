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

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
CWD=$(pwd)

rm -rf $STOCKS_ROOT/deploy-client/{pkg,src,stocks.tar}

mkdir -p $STOCKS_ROOT/deploy-client/target

tar -cf $STOCKS_ROOT/deploy-client/stocks.tar \
        --exclude=android-client \
        --exclude=target \
        --exclude=./server \
        --exclude=deploy-server \
        --exclude=.git \
        $STOCKS_ROOT

cd $STOCKS_ROOT/deploy-client
makepkg -fc
cd $CWD

if [[ -z $NO_SIGNATURE ]] ; then
    gpg --detach-sign --use-agent $STOCKS_ROOT/deploy-client/*.xz
    mv $STOCKS_ROOT/deploy-client/*.sig $STOCKS_ROOT/deploy-client/target
fi

mv $STOCKS_ROOT/deploy-client/*.xz $STOCKS_ROOT/deploy-client/target
rm -rf $STOCKS_ROOT/deploy-client/{pkg,src,stocks.tar}

