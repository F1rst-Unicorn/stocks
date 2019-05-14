package de.njsm.stocks.android.frontend.device;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class ServerTicket implements Parcelable {

    public static final Parcelable.Creator<ServerTicket> CREATOR = new Parcelable.Creator<ServerTicket>() {

        @Override
        public ServerTicket createFromParcel(Parcel source) {
            return new ServerTicket(source);
        }

        @Override
        public ServerTicket[] newArray(int size) {
            return new ServerTicket[size];
        }
    };

    public int deviceId;

    public String ticket;

    public ServerTicket(Parcel p) {
        deviceId = p.readInt();
        ticket = p.readString();
    }

    public ServerTicket() {
    }

    public ServerTicket(int deviceId, String ticket) {
        this.deviceId = deviceId;
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "ServerTicket{" +
                "deviceId=" + deviceId +
                ", ticket='" + ticket + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(deviceId);
        dest.writeString(ticket);
    }
}
