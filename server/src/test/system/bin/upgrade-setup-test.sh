#!/usr/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/resources/

source $STOCKS_ROOT/server/src/test/system/lib/lib.sh

if [[ $# -ne 1 ]] ; then
        echo "Usage: $0 <hostname/IP>"
        exit 1
fi

if [[ -e $RESOURCES/ca.crt ]] ; then
    echo "Setup has already run!"
    exit 2
fi

SERVER=$1

set -e
mkdir -p $RESOURCES

createFirstUser $RESOURCES
checkInitialServer
checkLocations
checkUsers
checkFood
checkEanNumbers
checkFoodItems
checkDevicesAndRevocation
cleanUpServer

echo SUCCESS
