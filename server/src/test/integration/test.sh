#!/usr/bin/bash

# Call on exit or failure to cleanup
cleanup() {
        rm -rf resources
}

check() {
        set +e
        egrep $1 resources/curl
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
curl http://$SERVER:10910/ca > resources/ca.crt
curl http://$SERVER:10910/chain > resources/ca-chain.crt

openssl x509 -in resources/ca.crt -text >/dev/null
openssl x509 -in resources/ca-chain.crt -text >/dev/null

openssl genrsa -out resources/client.key.pem 4096
openssl req -new -sha256 -key resources/client.key.pem \
        -out resources/client.csr.pem \
        -subj '/CN=Jan$1$Laptop$1' -batch
CSR=$(cat resources/client.csr.pem | tr \\n \& | sed 's/&/\\n/g')

curl -XPOST --data "{\"deviceId\": 1, \"ticket\": \"0000\", \"pemFile\": \
        \"$CSR\"}" \
        --cacert resources/ca-chain.crt \
        --insecure \
        --header 'content-type: application/json' \
        https://$SERVER:10911/uac/newuser > resources/response.json
cat resources/response.json | \
        sed 's/.*pemFile":.*"\(.*\)".*/\1/g' | \
        sed 's/\\n/%/g' | tr \% \\n > resources/client.crt.pem

openssl x509 -in resources/client.crt.pem -text >/dev/null

# check initial database
curl $CURLARGS -XGET https://192.168.100.5:10912/user > resources/curl
check '"id":1.*"name":"Jan"'
curl $CURLARGS -XGET https://192.168.100.5:10912/device > resources/curl
check '"id":1.*"name":"Laptop"'
curl $CURLARGS -XGET https://192.168.100.5:10912/location > resources/curl
check '^[]$'
curl $CURLARGS -XGET https://192.168.100.5:10912/food > resources/curl
check '^[]$'
curl $CURLARGS -XGET https://192.168.100.5:10912/food/item > resources/curl
check '^[]$'




cleanup
echo SUCCESS
