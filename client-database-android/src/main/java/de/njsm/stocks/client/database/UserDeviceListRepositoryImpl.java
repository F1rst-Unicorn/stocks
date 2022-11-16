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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.EntityDeleteRepository;
import de.njsm.stocks.client.business.UserDeviceListRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

class UserDeviceListRepositoryImpl implements UserDeviceListRepository, EntityDeleteRepository<UserDevice> {

    private final UserDeviceDao userDeviceDao;

    private final UserDao userDao;

    @Inject
    UserDeviceListRepositoryImpl(UserDeviceDao userDeviceDao, UserDao userDao) {
        this.userDeviceDao = userDeviceDao;
        this.userDao = userDao;
    }

    @Override
    public Observable<UserDevicesForListing> getUserDevices(Id<User> userId) {
        var devices = userDeviceDao.getUserDevices(userId.id());
        var user = userDao.getUser(userId.id());

        return devices.zipWith(user, (d, u) -> UserDevicesForListing.create(d, u.name()));
    }

    @Override
    public UserDeviceForDeletion getEntityForDeletion(Id<UserDevice> id) {
        return userDeviceDao.get(id.id());
    }
}
