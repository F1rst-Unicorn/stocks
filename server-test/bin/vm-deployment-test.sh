#!/bin/bash

# stocks is client-server program to manage a household's food stock
# Copyright (C) 2019  The stocks developers
#
# This file is part of the stocks program suite.
#
# stocks is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# stocks is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
DEPLOYMENT_VM="${DEPLOYMENT_VM:-dp-server}"

set -e

rm -rf $STOCKS_ROOT/server/target/server.log

cd "$STOCKS_ROOT/deploy-server"

sed -i "s/dp-server/$DEPLOYMENT_VM/g" $STOCKS_ROOT/deploy-server/inventory
ansible-playbook --extra-vars "ansible_become_pass=,ansible_sudo_pass= "     \
        $STOCKS_ROOT/deploy-server/play_install.yml
git checkout -- $STOCKS_ROOT/deploy-server/inventory

cd "$STOCKS_ROOT"
