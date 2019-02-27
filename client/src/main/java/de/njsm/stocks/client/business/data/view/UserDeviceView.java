package de.njsm.stocks.client.business.data.view;

import de.njsm.stocks.client.business.data.VersionedData;
import de.njsm.stocks.client.business.data.visitor.AbstractVisitor;

public class UserDeviceView extends VersionedData {

    public String name;

    public String user;

    public int userId;

    public UserDeviceView() {
    }

    public UserDeviceView(String name, String user, int userId) {
        this.name = name;
        this.user = user;
        this.userId = userId;
    }

    public UserDeviceView(int id, int version) {
        super(id, version);
    }

    public UserDeviceView(int id, int version, String name, String user, int userId) {
        super(id, version);
        this.name = name;
        this.user = user;
        this.userId = userId;
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.userDeviceView(this, arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDeviceView that = (UserDeviceView) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (!name.equals(that.name)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + userId;
        return result;
    }

    @Override
    public String toString() {
        return "UserDeviceView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user='" + user + '\'' +
                ", userId=" + userId +
                '}';
    }
}
