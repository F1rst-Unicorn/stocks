#!/bin/bash

ssh dp-server "sudo systemctl stop tomcat8;
        sudo pacman -Rsn stocks-server --noconfirm;
        sudo rm -rf /usr/share/stocks-server;
        sudo -u postgres dropdb stocks;
        sudo -u postgres dropuser stocks;"

exit 0
