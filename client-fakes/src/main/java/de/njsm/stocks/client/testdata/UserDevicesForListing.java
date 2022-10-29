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

package de.njsm.stocks.client.testdata;


import de.njsm.stocks.client.business.entities.UserDeviceForListing;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.List;

public class UserDevicesForListing {

    private final BehaviorSubject<de.njsm.stocks.client.business.entities.UserDevicesForListing> data;

    public UserDevicesForListing(de.njsm.stocks.client.business.entities.UserDevicesForListing data) {
        this.data = BehaviorSubject.createDefault(data);
    }

    public static de.njsm.stocks.client.business.entities.UserDevicesForListing generate() {
        return de.njsm.stocks.client.business.entities.UserDevicesForListing.create(
                List.of(
                        UserDeviceForListing.create(1, "Mobile"),
                        UserDeviceForListing.create(2, "Tablet"),
                        UserDeviceForListing.create(4, "Laptop")
                ),
                "Jack"
        );
    }

    public BehaviorSubject<de.njsm.stocks.client.business.entities.UserDevicesForListing> getData() {
        return data;
    }
}
