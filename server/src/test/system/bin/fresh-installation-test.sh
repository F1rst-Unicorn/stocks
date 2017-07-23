#!/usr/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/tmp/

source $STOCKS_ROOT/server/src/test/system/lib/lib.sh

if [[ $# -ne 1 ]] ; then
        echo "Usage: $0 <hostname/IP>"
        exit 1
fi

SERVER=$1

set -e
mkdir -p $RESOURCES

createFirstUser $RESOURCES
checkInitialServer
checkUpdates
checkLocations
checkUsers
checkFood
checkEanNumbers
checkFoodItems
checkDevicesAndRevocation

rm -rf $RESOURCES

echo SUCCESS
