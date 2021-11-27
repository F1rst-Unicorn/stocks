/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.clientold.init.upgrade;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    public static final Version PRE_VERSIONED = new Version(0, 0, 0);

    public static final Version V_0_5_0 = new Version(0, 5, 0);

    public static final Version V_2_0_3 = new Version(2, 0, 3);

    public static final Version V_3_0_0 = new Version(3, 0, 0);

    public static final Version V_3_0_1 = new Version(3, 0, 1);

    public static final Version CURRENT = V_3_0_1;

    private final int major;

    private final int minor;

    private final int patch;

    Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return new EqualsBuilder()
                .append(major, version.major)
                .append(minor, version.minor)
                .append(patch, version.patch)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(major)
                .append(minor)
                .append(patch)
                .toHashCode();
    }

    @Override
    public int compareTo(Version o) {
        return new CompareToBuilder()
                .append(this.major, o.major)
                .append(this.minor, o.minor)
                .append(this.patch, o.patch)
                .toComparison();
    }

    public static Version create(String value) {
        try {
            Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
            Matcher matcher = pattern.matcher(value);

            if (matcher.matches()) {
                int major = Integer.parseInt(matcher.group(1));
                int minor = Integer.parseInt(matcher.group(2));
                int patch = Integer.parseInt(matcher.group(3));
                return new Version(major, minor, patch);
            } else {
                throw new IllegalStateException("No match found");
            }
        } catch (NumberFormatException |
                IllegalStateException |
                IndexOutOfBoundsException e) {
            return PRE_VERSIONED;
        }
    }
}
