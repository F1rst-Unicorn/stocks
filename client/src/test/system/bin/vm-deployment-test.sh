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

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/tmp/

DEVICE_ID=$(cat $STOCKS_ROOT/server-test/target/01_id)
TICKET_VALUE=$(cat $STOCKS_ROOT/server-test/target/01_ticket)

set -e

rm -rf $STOCKS_ROOT/client/target/client-server.log
rm -rf $STOCKS_ROOT/client/target/client-client.log

cd "$STOCKS_ROOT/deploy-client"
ansible-playbook $STOCKS_ROOT/deploy-client/install.yml
cd -

echo "##teamcity[testSuiteStarted name='Client System Test']"

echo "##teamcity[testStarted name='Initialisation']"
FINGERPRINT=$(curl -s http://dp-server:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')

echo -e "dp-server\n\n\n\nJack\ncli-client\n1\n$DEVICE_ID\n\
$FINGERPRINT\n\
$TICKET_VALUE\nquit\n" | \
        ssh dp-client stocks
echo "##teamcity[testFinished name='Initialisation']"

python $STOCKS_ROOT/client/src/test/system/bin/testcase-driver.py \
        `find $STOCKS_ROOT/client/src/test/system/usecases -type f | sort`

scp dp-server:/var/log/tomcat8/stocks.log \
        $STOCKS_ROOT/client/target/client-server.log
scp dp-client:\~/.stocks/stocks.log $STOCKS_ROOT/client/target/client-client.log

echo "##teamcity[testSuiteFinished name='Client System Test']"
echo

