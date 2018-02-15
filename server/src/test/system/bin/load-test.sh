#!/usr/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/resources/

source $STOCKS_ROOT/server/src/test/system/lib/lib.sh

if [[ $# -ne 3 ]] ; then
        echo "Usage: $0 <hostname/IP> <number of clients> <requests per client>"
        exit 1
fi

SERVER=$1

if [[ -e $RESOURCES/ca.crt ]] ; then
    echo "Setup has already run"
else
    mkdir -p $RESOURCES
    createFirstUser $RESOURCES

fi

addFood
addLocation
addUser
addDevice

for client in $(seq 1 $2) ; do
    for request in $(seq 1 $3) ; do

        time case $(( $RANDOM % 11 )) in
            0)
                addFood
            ;;
            1)
                addLocation
            ;;
            2)
                addUser
            ;;
            3)
                addDevice
            ;;
            4)
                addEan
            ;;
            5)
                getFood
            ;;
            6)
                getFoodItems
            ;;
            7)
                getLocations
            ;;
            8)
                getUsers
            ;;
            9)
                getDevices
            ;;
            10)
                getEan
            ;;
        esac
    done &
    PIDs="$PIDs $!"
done

for pid in "$PIDs" ; do
    wait $pid
done
