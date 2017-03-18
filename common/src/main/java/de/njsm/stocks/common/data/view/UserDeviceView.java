package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.Data;

public class UserDeviceView extends Data {

    public int id;

    public String name;

    public String user;

    public UserDeviceView(int id, String name, String user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public UserDeviceView() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDeviceView that = (UserDeviceView) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserDeviceView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
