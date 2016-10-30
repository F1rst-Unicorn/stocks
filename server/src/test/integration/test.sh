#!/usr/bin/bash

# Call on exit or failure to cleanup
cleanup() {
        rm -rf resources
}

check() {
        set +e
        egrep "$1" resources/curl > /dev/null
        if [[ $? -ne 0 ]] ; then
                echo "ERROR: Expected $1"
                echo -n "       Actual "
                cat resources/curl
                echo
                exit 1
        fi
        set -e
}


CURLARGS="--cacert resources/ca-chain.crt --insecure --key resources/client.key.pem --cert resources/client.crt.pem"

if [[ $# -ne 1 ]] ; then
        echo "Usage: $0 <hostname/IP>"
        exit 1
fi

SERVER=$1

set -e
mkdir -p resources

# Create first user
curl -sS http://$SERVER:10910/ca > resources/ca.crt
curl -sS http://$SERVER:10910/chain > resources/ca-chain.crt

openssl x509 -in resources/ca.crt -text >/dev/null
openssl x509 -in resources/ca-chain.crt -text >/dev/null

openssl genrsa -out resources/client.key.pem 4096 2>/dev/null
openssl req -new -sha256 -key resources/client.key.pem \
        -out resources/client.csr.pem \
        -subj '/CN=Jan$1$Laptop$1' -batch
CSR=$(cat resources/client.csr.pem | tr \\n \& | sed 's/&/\\n/g')

curl -sS -XPOST --data "{\"deviceId\": 1, \"ticket\": \"0000\", \"pemFile\": \
        \"$CSR\"}" \
        --cacert resources/ca-chain.crt \
        --insecure \
        --header 'content-type: application/json' \
        https://$SERVER:10911/uac/newuser > resources/response.json
cat resources/response.json | \
        sed -r 's/.*pemFile":.*"(.*)".*/\1/g' | \
        sed 's/\\n/%/g' | tr \% \\n > resources/client.crt.pem

openssl x509 -in resources/client.crt.pem -text >/dev/null
rm resources/response.json

# Test invalid ticket rejection
curl -sS -XPOST --data "{\"deviceId\": 1, \"ticket\": \"0000\", \"pemFile\": \
        \"$CSR\"}" \
        --cacert resources/ca-chain.crt \
        --insecure \
        --header 'content-type: application/json' \
        https://$SERVER:10911/uac/newuser > resources/curl
check '^\{"deviceId":1,"ticket":"0000"\}$'

# check initial database
curl -sS $CURLARGS -XGET https://$SERVER:10912/user > resources/curl # FIXME: remove this once server-00018 is solved
curl -sS $CURLARGS -XGET https://$SERVER:10912/user > resources/curl
check '"id":1.*"name":"Jan"'
curl -sS $CURLARGS -XGET https://$SERVER:10912/device > resources/curl
check '"id":1.*"name":"Laptop"'
curl -sS $CURLARGS -XGET https://$SERVER:10912/location > resources/curl
check '^\[\]$'
curl -sS $CURLARGS -XGET https://$SERVER:10912/food > resources/curl
check '^\[\]$'
curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > resources/curl
check '^\[\]$'

# check location stuff
curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"Fridge"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/location > resources/curl
check '^\[\{"id":[0-9]+,"name":"Fridge"\}\]$'
ID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),.*/\1/g')
curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/Cupboard \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID,\"name\":\"Fridge\"}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/location > resources/curl
check "^\[\{\"id\":$ID,\"name\":\"Cupboard\"\}\]$"
curl -sS $CURLARGS -XPUT https://$SERVER:10912/location/remove \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID,\"name\":\"Cupboard\"}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/location > resources/curl
check '^\[\]$'
curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"Fridge"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/location > resources/curl
check '^\[\{"id":[0-9]+,"name":"Fridge"\}\]$'
LOCID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),.*/\1/g')

# check user stuff
curl -sS $CURLARGS -XPUT https://$SERVER:10912/user \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"Second user"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/user > resources/curl
check '^\[.*\{"id":[0-9]+,"name":"Second user"\}.*\]$'
ID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),"name":"Second.*/\1/g')
curl -sS $CURLARGS -XPUT https://$SERVER:10912/user/remove \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID,\"name\":\"Second user\"}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/user > resources/curl
check '^\[\{"id":[0-9]+,"name":"Jan"\}\]$'
curl -sS $CURLARGS -XPUT https://$SERVER:10912/user \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"John"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/user > resources/curl
check '^\[.*\{"id":[0-9]+,"name":"John"\}.*\]$'
USERID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),"name":"John.*/\1/g')


# check food stuff
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"Sausage"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/food > resources/curl
check '^\[\{"id":[0-9]+,"name":"Sausage"\}\]$'
ID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),.*/\1/g')
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/Bread \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID,\"name\":\"Sausage\"}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/food > resources/curl
check "^\[\{\"id\":$ID,\"name\":\"Bread\"\}\]$"
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/remove \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID,\"name\":\"Bread\"}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/food > resources/curl
check '^\[\]$'
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"Bread"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/food > resources/curl
check '^\[\{"id":[0-9]+,"name":"Bread"\}\]$'
FOODID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),.*/\1/g')


# check item stuff
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem \
        --header 'content-type: application/json' \
        --data "{\"id\":0,
                 \"eatByDate\":\"2017-01-01 00:00:00\",
                 \"ofType\":$FOODID,
                 \"storedIn\":$LOCID,
                 \"registers\":1,
                 \"buys\":1}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > resources/curl
check "^\[\{\"id\":[0-9]+,\"eatByDate\":\"2017-01-01\",\"ofType\":$FOODID,\"storedIn\":$LOCID,\"registers\":1,\"buys\":1\}\]"
ID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),.*/\1/g')
curl -sS $CURLARGS -XPUT https://$SERVER:10912/location \
        --header 'content-type: application/json' \
        --data '{"id":0,"name":"Cupboard"}'
curl -sS $CURLARGS -XGET https://$SERVER:10912/location > resources/curl
LOCTWOID=$(cat resources/curl | sed -r 's/.*"id":([0-9]+),"name":"Cup.*/\1/g')
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem/move/$LOCTWOID \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > resources/curl
check "^\[\{\"id\":[0-9]+,\"eatByDate\":\"2017-01-01\",\"ofType\":$FOODID,\"storedIn\":$LOCTWOID,\"registers\":1,\"buys\":1\}\]"
curl -sS $CURLARGS -XPUT https://$SERVER:10912/food/fooditem/remove \
        --header 'content-type: application/json' \
        --data "{\"id\":$ID}"
curl -sS $CURLARGS -XGET https://$SERVER:10912/food/fooditem > resources/curl
check '^\[\]$'


# check device stuff
 
# check ticket system

cleanup
echo SUCCESS
