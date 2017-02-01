#!/bin/bash

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
CWD=$(pwd)

mkdir -p $STOCKS_ROOT/deploy-server/target

tar -cf $STOCKS_ROOT/deploy-server/stocks.tar \
        --exclude=android-client \
        --exclude=client \
        --exclude=deploy-client \
        --exclude=target \
        $STOCKS_ROOT

cd $STOCKS_ROOT/deploy-server
makepkg -fc
cd $CWD

gpg --detach-sign --use-agent $STOCKS_ROOT/deploy-server/*.xz

mv $STOCKS_ROOT/deploy-server/*.sig $STOCKS_ROOT/deploy-server/target
mv $STOCKS_ROOT/deploy-server/*.xz $STOCKS_ROOT/deploy-server/target
rm -rf $STOCKS_ROOT/deploy-server/src \
        $STOCKS_ROOT/deploy-server/pkg \
        $STOCKS_ROOT/deploy-server/*.tar

