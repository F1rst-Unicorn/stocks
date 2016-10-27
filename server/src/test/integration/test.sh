#!/usr/bin/bash

set -e

SERVER=192.168.100.5

mkdir -p resources

# Create first user

curl http://$SERVER:10910/ca > resources/ca.crt








rm -rf resources

echo SUCCESS
