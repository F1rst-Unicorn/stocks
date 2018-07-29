package de.njsm.stocks.server.util;


import com.netflix.hystrix.exception.HystrixBadRequestException;

import java.util.Objects;

/**
 * Stores the principals of a user during ticket
 * creation
 */
public class Principals {

    private String username;
    private String deviceName;
    private int uid;
    private int did;

    public Principals(String[] rawInput) {

        if (rawInput.length != 4) {
            throw new HystrixBadRequestException("bad request", new SecurityException("client name malformed"));
        }

        username = rawInput[0];
        deviceName = rawInput[2];
        try {
            uid = Integer.parseInt(rawInput[1]);
            did = Integer.parseInt(rawInput[3]);
        } catch (NumberFormatException e) {
            throw new HystrixBadRequestException("bad request", new SecurityException("client IDs are invalid"));
        }
    }

    public Principals(String username, String deviceName, int uid, int did) {
        this.username = username;
        this.deviceName = deviceName;
        this.uid = uid;
        this.did = did;
    }

    public Principals(String username, String deviceName, String uid, String did) {
        this(new String[] {username, uid, deviceName, did});
    }

    public String getUsername() {
        return username;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getUid() {
        return uid;
    }

    public int getDid() {
        return did;
    }

    public String getReadableString() {
        return username + "@" + deviceName;
    }

    @Override
    public String toString() {
        return username + "$" + uid + "$" + deviceName + "$" + did;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Principals that = (Principals) o;
        return uid == that.uid &&
                did == that.did &&
                Objects.equals(username, that.username) &&
                Objects.equals(deviceName, that.deviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, deviceName, uid, did);
    }

    public static boolean isNameValid(String name) {
        int noDollar = name.indexOf('$');
        int noEqual  = name.indexOf('=');
        return noDollar == -1 && noEqual == -1;
    }
}
