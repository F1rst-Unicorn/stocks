#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

set -e

# virsh reset
sudo virsh snapshot-revert dp-client-server clean-running
sleep 1

ansible-playbook -e "target_host=dp-client-server" \
        $STOCKS_ROOT/deploy-server/install.yml
ansible-playbook \
        -e "target_host=dp-client-server stocks_user=Jack stocks_device=Device"\
        $STOCKS_ROOT/deploy-server/deploy.yml

rm -rf $STOCKS_ROOT/client/src/test/system/tmp
mkdir -p $STOCKS_ROOT/client/src/test/system/tmp/.stocks
echo ".read $STOCKS_ROOT/deploy-client/config/schema.sql" | \
        sqlite3 $STOCKS_ROOT/client/src/test/system/tmp/.stocks/stocks.db

FINGERPRINT=$(curl -s http://dp-client-server:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')

echo -e "dp-client-server\n\n\n\nJack\nDevice\n1\n1\n\
$FINGERPRINT\n\
0000\nrefresh\nuser\ndev\nfood\nloc\nquit\n" \
        | java -jar -Duser.stocks.dir=$STOCKS_ROOT/client/src/test/system/tmp \
        $STOCKS_ROOT/client/target/client-*-jar-with-dependencies.jar

rm -rf $STOCKS_ROOT/client/src/test/system/tmp
sudo virsh snapshot-revert dp-client-server clean

