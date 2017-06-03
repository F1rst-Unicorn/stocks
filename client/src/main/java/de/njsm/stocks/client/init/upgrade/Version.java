package de.njsm.stocks.client.init.upgrade;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    public static final Version PRE_VERSIONED = new Version(0, 0, 0);

    public static final Version V_0_5_0 = new Version(0, 5, 0);

    public static final Version V_0_5_1 = new Version(0, 5, 1);

    public static final Version CURRENT = V_0_5_1;

    private int major;

    private int minor;

    private int patch;

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
