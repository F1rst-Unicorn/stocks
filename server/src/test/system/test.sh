#!/bin/bash

WD=$(pwd)

set -e

# virsh reset
sudo virsh net-start custom >/dev/null 2>&1 || true
sudo virsh snapshot-revert dp-server clean-running

sleep 1

ansible-playbook ../../../../deploy-server/install.yml
ansible-playbook ../../../../deploy-server/deploy.yml

./system-test.sh dp-server

