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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.BusinessGettable;
import de.njsm.stocks.server.v2.business.DeviceManager;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/device")
@RestController
@RequestScope
public class DeviceEndpoint extends Endpoint implements Get<UserDeviceRecord, UserDevice> {

    private final DeviceManager manager;

    public DeviceEndpoint(DeviceManager manager) {
        this.manager = manager;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON)
    public DataResponse<NewDeviceTicket> putDevice(
            @RequestParam("name") String name,
            @RequestParam("belongsTo") int userId) {

        if (isValidName(name, "name") &&
                isValid(userId, "userId")) {
            Validation<StatusCode, NewDeviceTicket> result = manager.addDevice(UserDeviceForInsertion.builder()
                    .name(name)
                    .belongsTo(userId)
                    .build());
            return new DataResponse<>(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON)
    public Response deleteDevice(@RequestParam("id") int id,
                                 @RequestParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            StatusCode result = manager.delete(UserDeviceForDeletion.builder()
                    .id(id)
                    .version(version)
                    .build());
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @DeleteMapping(path = "/revoke", produces = MediaType.APPLICATION_JSON)
    public Response revokeDevice(@RequestParam("id") int id,
                                 @RequestParam("version") int version) {
        if (isValid(id, "id") &&
                isValidVersion(version, "version")) {

            StatusCode result = manager.revokeDevice(UserDeviceForDeletion.builder()
                    .id(id)
                    .version(version)
                    .build());
            return new Response(result);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }

    @Override
    public BusinessGettable<UserDeviceRecord, UserDevice> getManager() {
        return manager;
    }
}
