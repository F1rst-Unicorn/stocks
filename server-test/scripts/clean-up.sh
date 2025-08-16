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

DEPLOYMENT_VM="${DEPLOYMENT_VM:-dp-server}"

ssh $DEPLOYMENT_VM "sudo rm -rf /var/lib/tomcat10/webapp/stocks.war;
        while [ -d /usr/share/tomcat10/webapp/stocks ] ; do sleep 1 ; done;
        sudo systemctl restart tomcat10;
        sudo pacman -Rn stocks-server --noconfirm;
        sudo rm -rf /usr/share/stocks-server;
        sudo rm -rf /var/log/tomcat10/stocks-stocks.log;
        sudo touch /var/log/tomcat10/stocks-stocks.log;
        sudo chown tomcat10:tomcat10 /var/log/tomcat10/stocks-stocks.log
        sudo setfacl -m u:jan:r /var/log/tomcat10/stocks-stocks.log
        pgrep -U postgres -f 'postgres: .* stocks' | sudo xargs kill || true;
        sudo -u postgres dropdb stocks;
        sudo -u postgres dropuser stocks;"

exit 0
