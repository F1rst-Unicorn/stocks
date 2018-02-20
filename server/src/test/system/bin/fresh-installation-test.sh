#!/usr/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/tmp/
SERVER=$1

source $STOCKS_ROOT/server/src/test/system/lib/lib.sh

if [[ $# -ne 1 ]] ; then
        echo "Usage: $0 <hostname/IP>"
        exit 1
fi


set -e
rm -rf $RESOURCES
mkdir -p $RESOURCES

createFirstUser $RESOURCES
checkInvalidAccess
checkInitialServer
checkUpdates
checkLocations
checkUsers
checkFood
checkEanNumbers
checkFoodItems
checkDevicesAndRevocation
cleanUpServer


echo SUCCESS
