/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UserDeviceDeleteErrorDetails implements ErrorDetails {

    abstract IdImpl<UserDevice> idImpl();

    public Id<UserDevice> id() {
        return idImpl();
    }

    public abstract String userName();

    public abstract String deviceName();

    public static UserDeviceDeleteErrorDetails create(int id, String userName, String deviceName) {
        return new AutoValue_UserDeviceDeleteErrorDetails(IdImpl.create(id), userName, deviceName);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.userDeviceDeleteErrorDetails(this, input);
    }
}
