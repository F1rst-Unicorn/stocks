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

package de.njsm.stocks.client.network;


import de.njsm.stocks.client.business.EntityDeleteService;
import de.njsm.stocks.client.business.entities.UserDevice;
import de.njsm.stocks.client.business.entities.Versionable;
import de.njsm.stocks.common.api.Response;
import retrofit2.Call;

import javax.inject.Inject;

public class UserDeviceDeleteServiceImpl extends ServiceBase<Versionable<UserDevice>> implements EntityDeleteService<UserDevice> {

    @Inject
    UserDeviceDeleteServiceImpl(ServerApi api, CallHandler callHandler) {
        super(api, callHandler);
    }

    @Override
    Call<Response> buildCall(Versionable<UserDevice> entityForDeletion) {
        return api.deleteUserDevice(entityForDeletion.id(), entityForDeletion.version());
    }

    @Override
    public void delete(Versionable<UserDevice> entityForDeletion) {
        perform(entityForDeletion);
    }
}
