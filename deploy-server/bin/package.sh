#!/bin/bash

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
CWD=$(pwd)

mkdir -p $STOCKS_ROOT/deploy-server/target

tar -cvf $STOCKS_ROOT/deploy-server/stocks.tar \
        --exclude-vcs \
        --exclude=android-client \
        --exclude=client \
        --exclude=deploy-client \
        --exclude=.idea \
        $STOCKS_ROOT

cd $STOCKS_ROOT/deploy-server
makepkg -fc
cd $CWD

if [[ -z $NO_SIGNATURE ]] ; then
    gpg --detach-sign --use-agent $STOCKS_ROOT/deploy-server/*.xz
    mv $STOCKS_ROOT/deploy-server/*.sig $STOCKS_ROOT/deploy-server/target
fi

mv $STOCKS_ROOT/deploy-server/*.xz $STOCKS_ROOT/deploy-server/target
rm -rf $STOCKS_ROOT/deploy-server/src \
        $STOCKS_ROOT/deploy-server/pkg \
        $STOCKS_ROOT/deploy-server/*.tar

