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

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../"

emulators=("dp-android(AVD)\\ -\\ 7.0" "dp-android-ci(AVD)\\ -\\ 7.0" "GT-I9505\\ -\\ 11")
android_modules=(client-ui-android client-settings-android client-database-android client-navigation-android client-app-android)
modules=(client-core client-crypto client-network common)

echo -n "##teamcity[jacocoReport dataPath='"

for module in "${android_modules[@]}" ; do
  for emulator in "${emulators[@]}" ; do
    echo -n "$module/build/outputs/code_coverage/debugAndroidTest/connected/$emulator-coverage.ec "
  done
  echo -n "$module/build/jacoco/testCoverageUnitTest.exec "
done

for module in "${modules[@]}" ; do
  echo -n "$module/build/jacoco/test.exec "
done

echo -n "' includes='       \
    de.njsm.stocks.client.* \
    de.njsm.stocks.common.* \
' "

echo -n "excludes='            \
    de.njsm.stocks.clientold.* \
    *.AutoValue_*              \
    *.*Test                    \
    *.R                        \
    *.R.styleable              \
    *.*_Impl                   \
    *.*_Impl\$*                \
    *.*Directions              \
    *.BuildConfig              \
'"

echo ']'
