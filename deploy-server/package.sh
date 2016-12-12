#!/bin/bash

WD=$(dirname $0)

mkdir -p $WD/target

makepkg -f -p $WD/PKGBUILD-DEV --noextract


gpg --detach-sign --use-agent -u 36CF2994 *.xz

mv *.sig target
mv *.xz target
rm -rf src pkg

