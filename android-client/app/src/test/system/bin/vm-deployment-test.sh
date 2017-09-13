#!/bin/bash

STOCKS_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd )/../../../../../.."

sudo virsh snapshot-revert dp-server initialised-running || exit 1
sleep 1

cd $ANDROID_SDK
emulator -use-system-libs -avd TC &
cd -

ssh -L 10910:dp-server:10910 -N -o GatewayPorts=yes localhost &
SSH_1_PID=$!
ssh -L 10911:dp-server:10911 -N -o GatewayPorts=yes localhost &
SSH_2_PID=$!
ssh -L 10912:dp-server:10912 -N -o GatewayPorts=yes localhost &
SSH_3_PID=$!

adb wait-for-device
BOOTED=$(adb shell getprop sys.boot_completed | tr -d '\r')
while [ "$BOOTED" != "1" ]; do
        sleep 1
        BOOTED=$(adb shell getprop sys.boot_completed | tr -d '\r')
done

adb reverse tcp:10910 tcp:10910
adb reverse tcp:10911 tcp:10911
adb reverse tcp:10912 tcp:10912

$STOCKS_ROOT/android-client/gradlew -p $STOCKS_ROOT/android-client \
        connectedDebugAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=de.njsm.stocks.SystemTestSuite

kill $SSH_1_PID
kill $SSH_2_PID
kill $SSH_3_PID
echo -e "auth $(cat ~/.emulator_console_auth_token)\nkill\n" | nc localhost 5554

