/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.common.data.view;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

public class UserDeviceView extends Data {

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
