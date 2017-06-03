package de.njsm.stocks.client.init.upgrade;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Version implements Comparable<Version> {

    public static final Version PRE_VERSIONED = new Version(0, 0, 0);

    public static final Version V_0_5_0 = new Version(0, 5, 0);

    public static final Version CURRENT = V_0_5_0;

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
}
