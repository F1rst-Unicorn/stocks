#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."

# virsh reset
sudo virsh snapshot-revert dp-client-server initialised-running
sleep 1

mkdir -p $STOCKS_ROOT/client/src/test/system/tmp/.stocks
cp $STOCKS_ROOT/client/src/test/system/{stocks.db,tmp/.stocks/}

echo -e "dp-client-server\n\n\n\nJack\nDevice\n1\n1\n\
7F:40:EB:D6:91:A5:84:62:D4:48:5C:41:7A:7A:C8:FC:03:EB:92:C3:F3:F5:C7:39:9E:00:BC:3C:7D:FA:47:E8\n\
0000\nrefresh\nuser\ndevices\nfood\nlocation\nquit\n" \
        | java -jar -Duser.stocks.dir=$STOCKS_ROOT/client/src/test/system/tmp \
        $STOCKS_ROOT/client/target/client-*-jar-with-dependencies.jar

rm -rf $STOCKS_ROOT/client/src/test/system/tmp
sudo virsh snapshot-revert dp-client-server clean

