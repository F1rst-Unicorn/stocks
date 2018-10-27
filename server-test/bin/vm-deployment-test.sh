#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

rm -rf $STOCKS_ROOT/server/target/server.log

cd "$STOCKS_ROOT/deploy-server"

ansible-playbook --extra-vars "ansible_become_pass=,ansible_sudo_pass= "     \
        $STOCKS_ROOT/deploy-server/play_install.yml

cd "$STOCKS_ROOT"