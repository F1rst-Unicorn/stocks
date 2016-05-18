package de.njsm.stocks.sentry.data;


/**
 * Stores the principals of a user during ticket
 * creation
 */
public class Principals {

    protected String username;
    protected String deviceName;
    protected int uid;
    protected int did;

    public Principals(String[] rawInput) {

        if (rawInput.length != 4) {
            throw new SecurityException("client name malformed");
        }

        username = rawInput[0];
        deviceName = rawInput[2];
        try {
            uid = Integer.parseInt(rawInput[1]);
            did = Integer.parseInt(rawInput[3]);
        } catch (NumberFormatException e) {
            throw new SecurityException("client IDs are invalid");
        }
    }

    public Principals(String username, String deviceName, int uid, int did) {
        this.username = username;
        this.deviceName = deviceName;
        this.uid = uid;
        this.did = did;
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

    @Override
    public String toString() {
        return username + "$" + uid + "$" + deviceName + "$" + did;
    }
}
