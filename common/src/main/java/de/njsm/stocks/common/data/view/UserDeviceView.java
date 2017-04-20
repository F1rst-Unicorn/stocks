package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

public class UserDeviceView extends Data {

    public int id;

    public String name;

    public String user;

    public int userId;

    public UserDeviceView(int id, String name, String user, int userId) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.userId = userId;
    }

    public UserDeviceView() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.userDeviceView(this, input);
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
