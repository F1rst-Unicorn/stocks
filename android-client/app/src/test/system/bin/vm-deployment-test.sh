#!/bin/bash

STOCKS_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../../.."

LOGCAT=$STOCKS_ROOT/android-client/logcat.log

if [[ -z $CI_SERVER ]] ; then
        EMULATOR_ARGS=
else
        EMULATOR_ARGS="-no-window"
fi

if [[ -z $ANDROID_HOME ]] ; then
        echo "ANDROID_HOME is not set!"
        exit 1
fi

sudo virsh snapshot-revert dp-server initialised-running || exit 1
sleep 1

cd $ANDROID_HOME/tools
emulator $EMULATOR_ARGS -use-system-libs -avd dp-android &
DEVICE=emulator-5554
cd -

ssh -L 10910:dp-server:10910 -N -o GatewayPorts=yes localhost &
SSH_1_PID=$!
ssh -L 10911:dp-server:10911 -N -o GatewayPorts=yes localhost &
SSH_2_PID=$!
ssh -L 10912:dp-server:10912 -N -o GatewayPorts=yes localhost &
SSH_3_PID=$!

adb -s $DEVICE wait-for-device
BOOTED=$(adb -s $DEVICE shell getprop sys.boot_completed | tr -d '\r')
while [ "$BOOTED" != "1" ]; do
        sleep 1
        BOOTED=$(adb -s $DEVICE shell getprop sys.boot_completed | tr -d '\r')
done

adb -s $DEVICE reverse tcp:10910 tcp:10910
adb -s $DEVICE reverse tcp:10911 tcp:10911
adb -s $DEVICE reverse tcp:10912 tcp:10912
adb uninstall de.njsm.stocks
adb uninstall de.njsm.stocks.test
adb logcat | grep --line-buffered 'de.njsm.stocks' > $LOGCAT &
LOGCAT_PID=$!

RC=0
$STOCKS_ROOT/android-client/gradlew -p $STOCKS_ROOT/android-client \
        connectedDebugAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=de.njsm.stocks.SystemTestSuite
RC=$?

kill $SSH_1_PID
kill $SSH_2_PID
kill $SSH_3_PID
kill $LOGCAT_PID
echo -e "auth $(cat ~/.emulator_console_auth_token)\nkill\n" | nc localhost 5554

exit $RC
