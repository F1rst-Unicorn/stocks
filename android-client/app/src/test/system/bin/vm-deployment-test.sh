#!/bin/bash

# stocks is client-server program to manage a household's food stock
# Copyright (C) 2019  The stocks developers
#
# This file is part of the stocks program suite.
#
# stocks is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# stocks is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

STOCKS_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../../.."
RESOURCES=$STOCKS_ROOT/server/src/test/system/tmp/
SERVER="${SERVER:-dp-server-ci.j.njsm.de}"
DEVICE="${ANDROID_DEVICE:-emulator-5554}"
PROPERTIES=$STOCKS_ROOT/android-client/app/src/androidTest/java/de/njsm/stocks/android/test/system/Properties.java

DEVICE_ID=$(cat $STOCKS_ROOT/server-test/target/02_id)
TICKET_VALUE=$(cat $STOCKS_ROOT/server-test/target/02_ticket)
FINGERPRINT=$(curl -s http://dp-server:10910/ca | \
        openssl x509 -noout -sha256 -fingerprint | \
        head -n 1 | sed 's/.*=//')


LOGCAT=$STOCKS_ROOT/android-client/app/build/android-app.log
mkdir -p $STOCKS_ROOT/android-client/app/build
rm -rf $LOGCAT
rm -rf $STOCKS_ROOT/android-client/app/build/android-server.log

if [[ -z $ANDROID_SDK_ROOT ]] ; then
        echo "ANDROID_SDK_ROOT is not set!"
        exit 1
fi

adb -s $DEVICE wait-for-device
BOOTED=$(adb -s $DEVICE shell getprop sys.boot_completed | tr -d '\r')
while [ "$BOOTED" != "1" ]; do
        sleep 1
        BOOTED=$(adb -s $DEVICE shell getprop sys.boot_completed | tr -d '\r')
done

adb -s $DEVICE uninstall de.njsm.stocks || true
adb -s $DEVICE uninstall de.njsm.stocks.test || true
adb -s $DEVICE logcat | grep --line-buffered ' [VDIWEF] de\.njsm\.stocks\.' > $LOGCAT &
LOGCAT_PID=$!

sed -i "s/deviceId = 0/deviceId = $DEVICE_ID/g; \
    s/ticket = \"\"/ticket = \"$TICKET_VALUE\"/g; \
    s/server = \"\"/server = \"$SERVER\"/g; \
    s/fingerprint = \"\"/fingerprint = \"$FINGERPRINT\"/g" \
    $PROPERTIES

RC=0
$STOCKS_ROOT/android-client/gradlew -p $STOCKS_ROOT/android-client \
        connectedDebugAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=de.njsm.stocks.android.test.system.SystemTestSuite
RC=$?

git checkout $PROPERTIES

kill $LOGCAT_PID
killall adb

scp dp-server:/var/log/tomcat8/stocks-stocks.log \
    $STOCKS_ROOT/android-client/app/build/android-server.log

exit $RC
