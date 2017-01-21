#!/bin/bash

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
CWD=$(pwd)

mkdir -p $STOCKS_ROOT/deploy-client/target

tar -cf $STOCKS_ROOT/deploy-client/stocks.tar \
        --exclude=android-client \
        --exclude=target \
        --exclude=deploy-server \
        $STOCKS_ROOT

cd $STOCKS_ROOT/deploy-client
makepkg -fc
cd $CWD

gpg --detach-sign --use-agent $STOCKS_ROOT/deploy-client/*.xz

mv $STOCKS_ROOT/deploy-client/*.sig $STOCKS_ROOT/deploy-client/target
mv $STOCKS_ROOT/deploy-client/*.xz $STOCKS_ROOT/deploy-client/target
rm -rf $STOCKS_ROOT/deploy-client/src \
        $STOCKS_ROOT/deploy-client/pkg \
        $STOCKS_ROOT/deploy-client/*.tar

