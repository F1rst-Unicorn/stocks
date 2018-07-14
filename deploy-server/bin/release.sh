#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

if ! echo "$1" | egrep '^([0-9]+\.){3}[0-9]+-[0-9]+$' > /dev/null ; then
        echo Version number has wrong format!
        exit 1
fi

if ! git branch | grep "* master" >/dev/null ; then
        echo Release builds are only possible on master branch
        exit 2
fi

MAVEN_VERSION=$(echo "$1" | sed -r 's/([^.]+\.[^.]+).*/\1/g')
VERSION=$(echo "$1" | sed -r 's/(.*)-.*/\1/g')
RELEASE=$(echo "$1" | sed -r 's/.*-(.*)/\1/g')

if git tag | grep "server-$VERSION-$RELEASE" >/dev/null ; then
        echo This version has already been built
        exit 3
fi

echo Patching version number
sed "0,/version/{s$<version>.*</version>$<version>$MAVEN_VERSION</version>$}" \
        -i "$STOCKS_ROOT"/server/pom.xml
sed -i "s/pkgver=.*/pkgver=$VERSION/g" "$STOCKS_ROOT"/deploy-server/PKGBUILD
sed -i "s/pkgrel=.*/pkgrel=$RELEASE/g" "$STOCKS_ROOT"/deploy-server/PKGBUILD
sed -i "s/stocks_version: .*/stocks_version: $VERSION-$RELEASE/" \
        "$STOCKS_ROOT"/deploy-server/roles/stocks-server/vars/main.yml

echo Building release
export NO_SIGNATURE=""
"$STOCKS_ROOT"/deploy-server/bin/package.sh

echo Tagging release
git add -A
git commit -m "Increment server version to $VERSION-$RELEASE"
git tag -a "server-$VERSION-$RELEASE" -m \
        "Tagging server version $VERSION-$RELEASE"        
git push --all
git push --tags

echo Archive release
mkdir -p ~/Software/stocks/
cp "$STOCKS_ROOT"/deploy-server/target/stocks-server-$VERSION-$RELEASE-any.* \
        ~/Software/stocks/
chmod a-wx ~/Software/stocks/*

echo Publish release
scp "$STOCKS_ROOT"/deploy-server/target/stocks-server-$VERSION-$RELEASE-any.* \
        web-1.j.njsm.de:/tmp/
ssh -t web-1.j.njsm.de sudo /root/bin/publish-stocks

