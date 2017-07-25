#!/bin/bash

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

if ! echo "$1" | egrep '([0-9]+\.){3}[0-9]+' > /dev/null ; then
        echo Version number has wrong format!
        exit 1
fi

if ! git branch | grep "* master" >/dev/null ; then
        echo Release builds are only possible on master branch
        exit 2
fi

VERSION="$1"

if git tag | grep "android-client-$VERSION" >/dev/null ; then
        echo This version has already been built
        exit 3
fi

echo Patching version number
sed -i "s/versionName .*/versionName \"$VERSION\"/g" \
        "$STOCKS_ROOT"/android-client/app/build.gradle

echo Building release
STORE_FILE="$STOCKS_ROOT"/../keystore
echo -n "Password for keystore $STORE_FILE : "
read -n STOCKS_PASSWORD
KEY_ALIAS=stocks
KEY_PASSWORD="$STORE_PASSWORD"
"$STOCKS_ROOT"/android-client/gradlew assembleRelease \
        -Pandroid.injected.signing.store.file=$STORE_FILE \
        -Pandroid.injected.signing.store.password=$STORE_PASSWORD \
        -Pandroid.injected.signing.key.alias=$KEY_ALIAS \
        -Pandroid.injected.signing.key.password=$KEY_PASSWORD

echo Tagging release
git add -A
git commit -m "Increment android client version to $VERSION"
git tag -a "android-client-$VERSION" -m \
        "Tagging android client version $VERSION"
git push --all
git push --tags

echo Archive release
mkdir -p ~/Software/stocks/
cp "$STOCKS_ROOT"/android-client/app/build/outputs/apk/app-release-signed.apk  \
        ~/Software/stocks/stocks-android-"$VERSION".apk
chmod a-wx ~/Software/stocks/*

echo Publish release
scp ~/Software/stocks/stocks-android-"$VERSION".apk \
        web-1.vm-rv.j.njsm.de:/tmp/
ssh -t web-1.vm-rv.j.njsm.de sudo /root/bin/publish-stocks

