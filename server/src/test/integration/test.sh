#!/usr/bin/bash

# Call on exit or failure to cleanup
cleanup() {
        rm -rf resources
}

# Call curl helper
#
# $1: HTTP command
# $2: data for POST command
# $3: URL to send to
CURL() {

        if [[ $# -ne 3 ]] ; then
                exit 1
        fi

        if [[ $1 = POST ]] ; then
                curl -X"$1" --data "$2" $3
        else
                curl -X"$1" $3
        fi
}

if [[ $# -ne 1 ]] ; then
        echo "Usage: $0 <hostname/IP>"
        exit 1
fi

SERVER=$1

set -e

mkdir -p resources

# Create first user
curl http://$SERVER:10910/ca > resources/ca.crt
openssl genrsa -out resources/client.key.pem 4096
openssl req -new -sha256 -key resources/client.key.pem \
        -out resources/client.csr.pem \
        -subj '/CN=Jan$1$Laptop$1' -batch

CSR=$(cat resources/client.csr.pem | xargs echo -n)
curl -XPOST --data "{\"deviceId\": 1, \"ticket\": \"0000\", \"pemFile\": \
        \"$CSR\"}" \
        --cacert resources/ca.crt \
        https://$SERVER:10911/newuser > resources/response.json
cat resources/response.json | sed 's/.*pemFile":.*"\(.*\)".*/\1/g' \
        > resources/client.crt.pem





cleanup
echo SUCCESS
