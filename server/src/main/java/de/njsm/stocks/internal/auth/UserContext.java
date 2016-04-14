package de.njsm.stocks.internal.auth;

/**
 * This class stores principals of a connected user.
 */
public class UserContext {

    protected String name;
    protected int id;

    protected String deviceName;
    protected int deviceId;

    public UserContext(String name, int id, String deviceName, int deviceId) {
        this.name = name;
        this.id = id;
        this.deviceName = deviceName;
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
