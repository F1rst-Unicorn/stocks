#!/bin/bash

ssh dp-server sudo systemctl stop tomcat8
ssh dp-server sudo pacman -Rsn stocks-server --noconfirm

exit 0
