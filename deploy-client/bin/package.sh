#!/bin/bash

set -e

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."
CWD=$(pwd)

rm -rf $STOCKS_ROOT/deploy-client/{pkg,src,stocks.tar}

mkdir -p $STOCKS_ROOT/deploy-client/target

tar -cf $STOCKS_ROOT/deploy-client/stocks.tar \
        --exclude=android-client \
        --exclude=target \
        --exclude=./server \
        --exclude=deploy-server \
        --exclude=.git \
        $STOCKS_ROOT

cd $STOCKS_ROOT/deploy-client
makepkg -fc
cd $CWD

if [[ -z $NO_SIGNATURE ]] ; then
    gpg --detach-sign --use-agent $STOCKS_ROOT/deploy-client/*.xz
    mv $STOCKS_ROOT/deploy-client/*.sig $STOCKS_ROOT/deploy-client/target
fi

mv $STOCKS_ROOT/deploy-client/*.xz $STOCKS_ROOT/deploy-client/target
rm -rf $STOCKS_ROOT/deploy-client/{pkg,src,stocks.tar}

