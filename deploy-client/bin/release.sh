#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

set -e

if ! echo "$1" | egrep '^([0-9]+\.){3}[0-9]+-[0-9]+$' > /dev/null ; then
        echo Version number has wrong format!
        exit 1
fi

if ! git branch | grep "* master" >/dev/null ; then
        echo Release builds are only possible on master branch
        exit 2
fi

MAVEN_VERSION=$(echo "$1" | sed -r 's/([^.]+\.[^.]+).*/\1/g')
SQL_VERSION=$(echo "$1" | sed -r 's/([^.]+\.[^.]+\.[^.]+).*/\1/g')
JAVA_VERSION=$(echo "$SQL_VERSION" | sed 's/\./_/g')
JAVA_ARGUMENTS=$(echo "$SQL_VERSION" | sed 's/\./, /g')
VERSION=$(echo "$1" | sed -r 's/(.*)-.*/\1/g')
RELEASE=$(echo "$1" | sed -r 's/.*-(.*)/\1/g')

if git tag | grep "client-$VERSION-$RELEASE" >/dev/null ; then
        echo This version has already been built
        exit 3
fi

echo Patching version number
sed "0,/version/{s$<version>.*</version>$<version>$MAVEN_VERSION</version>$}" \
        -i "$STOCKS_ROOT"/client/pom.xml
sed -i "s/pkgver=.*/pkgver=$VERSION/g" "$STOCKS_ROOT"/deploy-client/PKGBUILD
sed -i "s/pkgrel=.*/pkgrel=$RELEASE/g" "$STOCKS_ROOT"/deploy-client/PKGBUILD
sed -i "s/CURRENT = .*/CURRENT = V_$JAVA_VERSION;/g; \
        s/\(.*CURRENT.*\)/    public static final Version \
V_$JAVA_VERSION = new Version($JAVA_ARGUMENTS);\n\n\1/g" \
        "$STOCKS_ROOT"/client/src/main/java/de/njsm/stocks/client/init/upgrade/\
Version.java
sed -i "s/.*db\.version.*/    ('db.version', '$SQL_VERSION')/g" \
        "$STOCKS_ROOT"/deploy-client/config/schema.sql
sed -i "s/stocks_version: .*/stocks_version: $VERSION-$RELEASE/" \
        "$STOCKS_ROOT"/deploy-client/install.yml

echo Building release
export NO_SIGNATURE=""
"$STOCKS_ROOT"/deploy-client/bin/package.sh

echo Tagging release
git add -A
git commit -m "Increment client version to $VERSION-$RELEASE"
git tag -a "client-$VERSION-$RELEASE" -m \
        "Tagging client version $VERSION-$RELEASE"        
git push --all
git push --tags

echo Archive release
mkdir -p ~/Software/stocks/
cp "$STOCKS_ROOT"/deploy-client/target/stocks-$VERSION-$RELEASE-any.* \
        ~/Software/stocks/
chmod a-wx ~/Software/stocks/*

echo Publish release
scp "$STOCKS_ROOT"/deploy-client/target/stocks-$VERSION-$RELEASE-any.* \
        web-1.j.njsm.de:/tmp/
ssh -t web-1.j.njsm.de sudo /root/bin/publish-stocks

