#!/bin/bash

ssh dp-client sudo pacman -Rsn stocks --noconfirm
ssh dp-client rm -rf \~/.stocks

exit 0
