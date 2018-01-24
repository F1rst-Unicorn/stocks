#!/usr/bin/bash

CURLARGS="--cacert $RESOURCES/ca-chain.crt \
        --insecure \
        --key $RESOURCES/client.key.pem \
        --cert $RESOURCES/client.crt.pem"

CURL_FILE=$RESOURCES/curl

# Verify whether the content of $RESOURCES/curl matches the regex given as arg
#
# Exit with an error message if the regex does not match
#
# arg 1: Regex to match
# arg 2: Name of the test
# arg 3: Optional: Set to "1" to invert the match
#
check() {
        GREPARGS=""
        MESSAGE=""
        if [[ "$3" == "1" ]] ; then
            GREPARGS="$GREPARGS -v"
            MESSAGE="Match is inverted"
        fi

        set +e
        egrep $GREPARGS "$1" $CURL_FILE > /dev/null
        if [[ $? -ne 0 ]] ; then
                echo "ERROR: Expected $1"
                echo -n "       Actual "
                echo "$MESSAGE"
                echo "##teamcity[testFailed name='$2' message='Comparison \
failed' expected='$1' actual='$(cat $CURL_FILE)' type='comparisonFailure']"
                cat $CURL_FILE
                echo
                exit 1
        fi
        set -e
}

fail() {
        echo $1
        exit 1
}

# arg 1: Root of the resources path
createFirstUser() {
    curl -sS http://$SERVER:10910/ca > $RESOURCES/ca.crt
    curl -sS http://$SERVER:10910/chain > $RESOURCES/ca-chain.crt

    openssl x509 -in $RESOURCES/ca.crt -text >/dev/null || \
            fail "ca.crt received from server is no valid certificate"
    openssl x509 -in $RESOURCES/ca-chain.crt -text >/dev/null || \
            fail "ca-chain.crt received from server is no valid certificate"

    openssl genrsa -out $RESOURCES/client.key.pem 4096 2>/dev/null
    openssl req -new -sha256 -key $RESOURCES/client.key.pem \
            -out $RESOURCES/client.csr.pem \
            -subj '/CN=Jack$1$Device$1' -batch
    CSR=$(cat $RESOURCES/client.csr.pem | tr \\n \& | sed 's/&/\\n/g')

    echo "##teamcity[testStarted name='Initialisation']"
    curl -sS -XPOST --data "{\"deviceId\": 1, \"ticket\": \"0000\", \"pemFile\": \
            \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $RESOURCES/response.json
    cat $RESOURCES/response.json | \
            sed -r 's/.*pemFile":.*"(.*)".*/\1/g' | \
            sed 's/\\n/%/g' | tr \% \\n > $RESOURCES/client.crt.pem

    openssl x509 -in $RESOURCES/client.crt.pem -text >/dev/null || \
            fail "client.crt.pem received from sentry is no valid certificate"
    rm $RESOURCES/response.json
    echo Test first user: OK
    echo "##teamcity[testFinished name='Initialisation']"
}

checkInvalidAccess() {
    NAME="Cannot access server via sentry port"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XGET https://$SERVER:10911/location > $CURL_FILE
    check '^.*404 Not Found.*$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Cannot access sentry via server port"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPOST https://$SERVER:10912/uac/newuser > $CURL_FILE
    check '^.*404 Not Found.*$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
}

checkInitialServer() {
    NAME="Users are initially empty"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '"id":1.*"name":"Jack"' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Devices are initially empty"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check '"id":1.*"name":"Device"' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    
    NAME="Locations are initially empty"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
   
    NAME="Food is initially empty"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
  
    NAME="Food items are initially empty"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    echo Test initial database: OK
}

checkUpdates() {
    NAME="Updates change on table change"
    echo "##teamcity[testStarted name='$NAME']"
    DATE='[0-9]{4}\.[0-9]{2}\.[0-9]{2}-[0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}-\+0000'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/update > $CURL_FILE
    check "^\[(\{\"table\":\"[^\"]+\",\"lastUpdate\":\"$DATE\"\},?)+\]$" "$NAME"
    BEFORE=$(cat $CURL_FILE | sed -r \
            's/.*"table":"Food","lastUpdate":"([^"]*)".*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Sausage"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    curl -sS $CURLARGS -XGET https://$SERVER:10912/update > $CURL_FILE
    check "^\[(\{\"table\":\"[^\"]+\",\"lastUpdate\":\"$DATE\"\},?)+\]$" "$NAME"
    check "^\[(.*\{\"table\":\"Food\",\"lastUpdate\":\"$BEFORE\"\},?)+\]$" \
            "$NAME" 1
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Sausage\"}"
    echo "##teamcity[testFinished name='$NAME']"
    echo Test updates: OK
}

checkLocations() {
    NAME="Add a location"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Fridge"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Fridge"\}\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    NAME="Rename a location"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/Cupboard \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Fridge\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check "^\[\{\"id\":$ID,\"name\":\"Cupboard\"\}\]$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Remove a location"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Cupboard\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Add a location again"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Fridge"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Fridge"\}\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    LOCID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    echo Test locations: OK
}

checkUsers() {

    NAME="Add a user"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Second user"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '^\[.*\{"id":[0-9]+,"name":"Second user"\}.*\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Second.*/\1/g')

    NAME="Remove a user"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Second user\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Jack"\}\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Add a user again"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"John"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '^\[.*\{"id":[0-9]+,"name":"John"\}.*\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    USERID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"John.*/\1/g')

    echo Test users: OK
}

checkFood() {
    NAME="Add a food type"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Sausage"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Sausage"\}\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    NAME="Rename a food type"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/Bread \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Sausage\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check "^\[\{\"id\":$ID,\"name\":\"Bread\"\}\]$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Remove a food type"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Bread\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Add a food type again"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Bread"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Bread"\}\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    FOODID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    echo Test food: OK
}

checkEanNumbers() {
    NAME="Add an EAN number"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean \
            --header 'content-type: application/json' \
            --data "{\"id\":0,
                     \"eanCode\":\"123-123-12345\",
                     \"identifiesFood\":$FOODID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/ean > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eanCode\":\"123-123-12345\",\
\"identifiesFood\":$FOODID\}\]$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    NAME="Remove an EAN number"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\
                    \"eanCode\":\"123-123-12345\",\
                    \"identifiesFood\":$FOODID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/ean > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Add an EAN number again"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean \
            --header 'content-type: application/json' \
            --data "{\"id\":0,
                     \"eanCode\":\"123-123-12345\",
                     \"identifiesFood\":$FOODID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/ean > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eanCode\":\"123-123-12345\",\
\"identifiesFood\":$FOODID\}\]$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    EANID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    echo Test EAN numbers: OK
}

checkFoodItems() {
    NAME="Add a food item"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem \
        --header 'content-type: application/json' \
        --data "{\"id\":0,
                 \"eatByDate\":\"2017.01.01-00:00:00.000-+0000\",
                 \"ofType\":$FOODID,
                 \"storedIn\":$LOCID,
                 \"registers\":1,
                 \"buys\":1}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eatByDate\":\"2017\.01\.01-00:00:00.000-\+0000\",\"ofType\":$FOODID,\
\"storedIn\":$LOCID,\"registers\":1,\"buys\":1\}\]" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')

    NAME="Move a food item"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Cupboard"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    LOCTWOID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Cup.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem/move/$LOCTWOID \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eatByDate\":\"2017\.01\.01-00:00:00.000-\+0000\",\"ofType\":$FOODID,\
\"storedIn\":$LOCTWOID,\"registers\":1,\"buys\":1\}\]" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Remove a food item"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check '^\[\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    echo Test food items: OK
}

checkDevicesAndRevocation() {

    NAME="Add a device"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/device \
            --header 'content-type: application/json' \
            --data "{\"id\":0,\"name\":\"Mobile\",\"userId\":$USERID}" \
            > $RESOURCES/newTicket.json
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check "^\[.*\{\"id\":[0-9]+,\"name\":\"Mobile\",\"userId\":$USERID\}.*\]$" \
            "$NAME"
    echo "##teamcity[testFinished name='$NAME']"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Mobile.*/\1/g')

    NAME="Remove a device"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/device/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Mobile\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check '^\[\{"id":1,"name":"Device","userId":1\}\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Add a device again"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/device \
            --header 'content-type: application/json' \
            --data "{\"id\":0,\"name\":\"Mobile\",\"userId\":$USERID}" \
            > $RESOURCES/newTicket.json
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    echo "##teamcity[testFinished name='$NAME']"
    check "^\[.*\{\"id\":[0-9]+,\"name\":\"Mobile\",\"userId\":$USERID\}.*\]$" \
            "$NAME"
    DEVID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Mobile.*/\1/g')
    TICKET=$(cat $RESOURCES/newTicket.json | sed -r 's/.*"ticket":"([^"]*)".*/\1/g')

    echo Test devices: OK

    openssl genrsa -out $RESOURCES/newClient.key.pem 4096 2>/dev/null

    NAME="Try registration with no ticket"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS -XPOST --data "{\"deviceId\": $DEVID, \"ticket\": \"\", \"pemFile\": \
    \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"\"\}$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Try registration with wrong ticket"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS -XPOST --data "{\"deviceId\": $DEVID, \"ticket\": \"f\", \"pemFile\": \
    \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"f\"\}$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Try registration with wrong device ID"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS -XPOST --data "{\"deviceId\":0,\"ticket\": \"$TICKET\", \"pemFile\": \
    \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":0,\"ticket\":\"$TICKET\"\}$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Try registration with wrong CSR common name"
    echo "##teamcity[testStarted name='$NAME']"
    openssl req -new -sha256 -key $RESOURCES/newClient.key.pem \
            -out $RESOURCES/wrong.csr.pem \
            -subj "/CN=John\$$USERID\$Mobile\$0" -batch
    WRONGCSR=$(cat $RESOURCES/wrong.csr.pem | tr \\n \& | sed 's/&/\\n/g')
    curl -sS -XPOST --data "{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\",\
    \"pemFile\":\"$WRONGCSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\"\}$" "$NAME"

    openssl req -new -sha256 -key $RESOURCES/newClient.key.pem \
            -out $RESOURCES/wrong.csr.pem \
            -subj "/CN=John\$$USERID\$Mobil\$$DEVID" -batch
    WRONGCSR=$(cat $RESOURCES/wrong.csr.pem | tr \\n \& | sed 's/&/\\n/g')
    curl -sS -XPOST --data "{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\",\
    \"pemFile\":\"$WRONGCSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\"\}$" "$NAME"
    openssl req -new -sha256 -key $RESOURCES/newClient.key.pem \
            -out $RESOURCES/wrong.csr.pem \
            -subj "/CN=Jack\$$USERID\$Mobile\$$DEVID" -batch
    WRONGCSR=$(cat $RESOURCES/wrong.csr.pem | tr \\n \& | sed 's/&/\\n/g')
    curl -sS -XPOST --data "{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\",\
    \"pemFile\":\"$WRONGCSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\"\}$" "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Correct registration"
    echo "##teamcity[testStarted name='$NAME']"
    openssl req -new -sha256 -key $RESOURCES/newClient.key.pem \
            -out $RESOURCES/newClient.csr.pem \
            -subj "/CN=John\$$USERID\$Mobile\$$DEVID" -batch
    CSR=$(cat $RESOURCES/newClient.csr.pem | tr \\n \& | sed 's/&/\\n/g')

    curl -sS -XPOST --data "{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\",\
    \"pemFile\":\"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $RESOURCES/response.json
    cat $RESOURCES/response.json | \
            sed -r 's/.*pemFile":.*"(.*)".*/\1/g' | \
            sed 's/\\n/%/g' | tr \% \\n > $RESOURCES/newClient.crt.pem
    openssl x509 -in $RESOURCES/newClient.crt.pem -text >/dev/null
    rm $RESOURCES/response.json
    echo "##teamcity[testFinished name='$NAME']"

    echo Test sentry: OK

    NAME="New client is able to retrieve data"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS -XGET --cacert $RESOURCES/ca-chain.crt \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure --key $RESOURCES/newClient.key.pem \
            --cert $RESOURCES/newClient.crt.pem \
            https://$SERVER:10912/location \
            > $CURL_FILE
    check '^\[.*\]$' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    NAME="Revoked users don|'t have access any more"
    echo "##teamcity[testStarted name='$NAME']"
    curl -sS -XPUT $CURLARGS --data "{\"id\":$DEVID}" \
            --header 'content-type: application/json' \
            https://$SERVER:10912/device/remove
    sleep 3
    curl -sS -XGET --cacert $RESOURCES/ca-chain.crt \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure --key $RESOURCES/newClient.key.pem \
            --cert $RESOURCES/newClient.crt.pem \
            https://$SERVER:10912/location \
            > $CURL_FILE
    check '400 The SSL certificate error' "$NAME"
    echo "##teamcity[testFinished name='$NAME']"

    echo Test revocation: OK
}

cleanUpServer() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$LOCID,\"name\":\"Fridge\"}"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$LOCTWOID,\"name\":\"Cupboard\"}"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$USERID,\"name\":\"Second user\"}"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$FOODID,\"name\":\"Bread\"}"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$EANID,\
                    \"eanCode\":\"123-123-12345\",\
                    \"identifiesFood\":$FOODID}"
}
