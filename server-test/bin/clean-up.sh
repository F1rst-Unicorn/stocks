#!/bin/bash

ssh dp-server sudo systemctl stop tomcat8
ssh dp-server sudo pacman -Rsn stocks-server --noconfirm
ssh dp-server sudo rm -rf /usr/share/stocks-server
ssh dp-server "mysql -u root \
        -e \"drop database if exists stocks;
            drop user if exists 'stocks'@'localhost'\""

exit 0
