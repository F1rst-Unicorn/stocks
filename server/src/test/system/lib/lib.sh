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
#
check() {
        set +e
        egrep "$1" $CURL_FILE > /dev/null
        if [[ $? -ne 0 ]] ; then
                echo "ERROR: Expected $1"
                echo -n "       Actual "
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

}


checkInitialServer() {
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '"id":1.*"name":"Jack"'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check '"id":1.*"name":"Device"'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\]$'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\]$'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check '^\[\]$'
    echo Test initial database: OK
}

checkLocations() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Fridge"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Fridge"\}\]$'
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/Cupboard \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Fridge\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check "^\[\{\"id\":$ID,\"name\":\"Cupboard\"\}\]$"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Cupboard\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\]$'
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Fridge"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Fridge"\}\]$'
    LOCID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    echo Test locations: OK
}

checkUsers() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Second user"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '^\[.*\{"id":[0-9]+,"name":"Second user"\}.*\]$'
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Second.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Second user\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Jack"\}\]$'
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/user \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"John"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/user > $CURL_FILE
    check '^\[.*\{"id":[0-9]+,"name":"John"\}.*\]$'
    USERID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"John.*/\1/g')
    echo Test users: OK
}

checkFood() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Sausage"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Sausage"\}\]$'
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/Bread \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Sausage\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check "^\[\{\"id\":$ID,\"name\":\"Bread\"\}\]$"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Bread\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\]$'
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Bread"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food > $CURL_FILE
    check '^\[\{"id":[0-9]+,"name":"Bread"\}\]$'
    FOODID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    echo Test food: OK
}

checkEanNumbers() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean \
            --header 'content-type: application/json' \
            --data "{\"id\":0,
                     \"eanCode\":\"123-123-12345\",
                     \"identifiesFood\":$FOODID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/ean > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eanCode\":\"123-123-12345\",\
\"identifiesFood\":$FOODID\}\]$"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\
                    \"eanCode\":\"123-123-12345\",\
                    \"identifiesFood\":$FOODID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/ean > $CURL_FILE
    check '^\[\]$'
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/ean \
            --header 'content-type: application/json' \
            --data "{\"id\":0,
                     \"eanCode\":\"123-123-12345\",
                     \"identifiesFood\":$FOODID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/ean > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eanCode\":\"123-123-12345\",\
\"identifiesFood\":$FOODID\}\]$"
    EANID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    echo Test EAN numbers: OK
}

checkFoodItems() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem \
        --header 'content-type: application/json' \
        --data "{\"id\":0,
                 \"eatByDate\":\"2017-01-01 00:00:00\",
                 \"ofType\":$FOODID,
                 \"storedIn\":$LOCID,
                 \"registers\":1,
                 \"buys\":1}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eatByDate\":\"2017-01-01\",\"ofType\":$FOODID,\
\"storedIn\":$LOCID,\"registers\":1,\"buys\":1\}\]"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
            --header 'content-type: application/json' \
            --data '{"id":0,"name":"Cupboard"}'
    curl -sS $CURLARGS -XGET https://$SERVER:10912/location > $CURL_FILE
    LOCTWOID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Cup.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem/move/$LOCTWOID \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check "^\[\{\"id\":[0-9]+,\"eatByDate\":\"2017-01-01\",\"ofType\":$FOODID,\
\"storedIn\":$LOCTWOID,\"registers\":1,\"buys\":1\}\]"
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > $CURL_FILE
    check '^\[\]$'
    echo Test food items: OK
}

checkDevicesAndRevocation() {
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/device \
            --header 'content-type: application/json' \
            --data "{\"id\":0,\"name\":\"Mobile\",\"userId\":$USERID}" \
            > $RESOURCES/newTicket.json
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check "^\[.*\{\"id\":[0-9]+,\"name\":\"Mobile\",\"userId\":$USERID\}.*\]$"
    ID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Mobile.*/\1/g')
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/device/remove \
            --header 'content-type: application/json' \
            --data "{\"id\":$ID,\"name\":\"Mobile\"}"
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check '^\[\{"id":1,"name":"Device","userId":1\}\]$'
    curl -sS $CURLARGS -XPUT https://$SERVER:10912/device \
            --header 'content-type: application/json' \
            --data "{\"id\":0,\"name\":\"Mobile\",\"userId\":$USERID}" \
            > $RESOURCES/newTicket.json
    curl -sS $CURLARGS -XGET https://$SERVER:10912/device > $CURL_FILE
    check "^\[.*\{\"id\":[0-9]+,\"name\":\"Mobile\",\"userId\":$USERID\}.*\]$"
    DEVID=$(cat $CURL_FILE | sed -r 's/.*"id":([0-9]+),"name":"Mobile.*/\1/g')
    TICKET=$(cat $RESOURCES/newTicket.json | sed -r 's/.*"ticket":"(.*)".*/\1/g')
    echo Test devices: OK

    # check ticket system
    openssl genrsa -out $RESOURCES/newClient.key.pem 4096 2>/dev/null

    ## test no ticket
    curl -sS -XPOST --data "{\"deviceId\": $DEVID, \"ticket\": \"\", \"pemFile\": \
    \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"\"\}$"
    ## test wrong ticket
    curl -sS -XPOST --data "{\"deviceId\": $DEVID, \"ticket\": \"f\", \"pemFile\": \
    \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"f\"\}$"
    ## test wrong device id
    curl -sS -XPOST --data "{\"deviceId\":0,\"ticket\": \"$TICKET\", \"pemFile\": \
    \"$CSR\"}" \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure \
            --header 'content-type: application/json' \
            https://$SERVER:10911/uac/newuser > $CURL_FILE
    check "^\{\"deviceId\":0,\"ticket\":\"$TICKET\"\}$"
    ## test wrong CSR
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
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\"\}$"
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
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\"\}$"
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
    check "^\{\"deviceId\":$DEVID,\"ticket\":\"$TICKET\"\}$"

    ## test correct ticket
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
    echo Test sentry: OK

    # test new client
    curl -sS -XGET --cacert $RESOURCES/ca-chain.crt \
            --cacert $RESOURCES/ca-chain.crt \
            --insecure --key $RESOURCES/newClient.key.pem \
            --cert $RESOURCES/newClient.crt.pem \
            https://$SERVER:10912/location \
            > $CURL_FILE
    check '^\[.*\]$'

    ## test revoke access
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
    check '400 The SSL certificate error'
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
