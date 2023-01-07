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

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

if ! echo "$1" | egrep '^([0-9]+\.){3}[0-9]+$' > /dev/null ; then
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
        "$STOCKS_ROOT"/client-app-android/build.gradle

sed -i -e "/## Unreleased/a ## [$VERSION]" -e "/## Unreleased/G" \
        "$STOCKS_ROOT/manual/android-client/CHANGELOG.md"

echo Tagging release
git add -A
git commit -m "Increment android client version to $VERSION"
zsh
git tag -a "android-client-$VERSION" -m \
        "Tagging android client version $VERSION"
git push build
git push build --tags
