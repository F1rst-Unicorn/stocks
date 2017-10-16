#!/bin/bash

ssh dp-client sudo pacman -Rsn stocks
ssh dp-client rm -rf \~/.stocks
