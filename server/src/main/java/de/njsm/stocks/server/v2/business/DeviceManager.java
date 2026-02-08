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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.TicketHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import fj.data.Validation;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.security.SecureRandom;

@Service
@RequestScope
public class DeviceManager extends BusinessObject<UserDeviceRecord, UserDevice> implements
        BusinessGettable<UserDeviceRecord, UserDevice>,
        BusinessDeletable<UserDeviceForDeletion, UserDevice> {

    private static final int TICKET_LENGTH = 64;

    private final UserDeviceHandler userDeviceHandler;

    private final FoodItemHandler foodItemHandler;

    private final TicketHandler ticketHandler;

    private final AuthAdmin authAdmin;

    public DeviceManager(UserDeviceHandler userDeviceHandler,
                         FoodItemHandler foodItemHandler,
                         TicketHandler ticketHandler,
                         AuthAdmin authAdmin) {
        super(userDeviceHandler);
        this.userDeviceHandler = userDeviceHandler;
        this.foodItemHandler = foodItemHandler;
        this.ticketHandler = ticketHandler;
        this.authAdmin = authAdmin;
    }

    public Validation<StatusCode, NewDeviceTicket> addDevice(UserDeviceForInsertion device) {
        return runFunction(() -> {

            Validation<StatusCode, Integer> deviceAddResult = userDeviceHandler.addReturningId(device);
            if (deviceAddResult.isFail())
                return Validation.fail(deviceAddResult.fail());

            int deviceId = deviceAddResult.success();
            String ticket = generateTicket();

            StatusCode ticketAddResult = ticketHandler.addTicket(deviceId, ticket);
            if (ticketAddResult != StatusCode.SUCCESS)
                return Validation.fail(ticketAddResult);

            NewDeviceTicket result = NewDeviceTicket.builder()
                    .deviceId(deviceAddResult.success())
                    .ticket(ticket)
                    .build();
            return Validation.success(result);
        });
    }

    public StatusCode delete(UserDeviceForDeletion device) {
        return runOperation(() -> removeDeviceInternally(device));
    }

    public StatusCode revokeDevice(UserDeviceForDeletion device) {
        return runOperation(() -> {
            var result = userDeviceHandler.setReadOnly();
            if (result.isFail()) {
                return result;
            }
            return authAdmin.revokeCertificate(device.id());
        });
    }

    StatusCode removeDeviceInternally(UserDeviceForDeletion device) {
        return checkTechnicalDeviceStatus(device)
                .bind(() -> checkIfInitiatingDevice(device))
                .bind(() -> foodItemHandler.transferFoodItems(device, getPrincipals().toDevice()))
                .bind(() -> ticketHandler.removeTicketOfDevice(device))
                .bind(() -> userDeviceHandler.delete(device))
                .bind(() -> authAdmin.revokeCertificate(device.id()));
    }

    private StatusCode checkIfInitiatingDevice(UserDeviceForDeletion device) {
        if (device.id() == getPrincipals().getDid()) {
            return StatusCode.ACCESS_DENIED;
        } else {
            return StatusCode.SUCCESS;
        }
    }

    private StatusCode checkTechnicalDeviceStatus(Identifiable<UserDevice> userDevice) {
        return userDeviceHandler.isTechnicalUser(userDevice).map(v -> {
            if (v)
                return StatusCode.ACCESS_DENIED;
            else
                return StatusCode.SUCCESS;
        }).validation(v -> v, v -> v);
    }

    private static String generateTicket() {
        SecureRandom generator = new SecureRandom();
        byte[] content = new byte[TICKET_LENGTH];

        for (int i = 0; i < TICKET_LENGTH; i++){
            content[i] = getNextByte(generator);
        }

        return new String(content);
    }

    private static byte getNextByte(SecureRandom generator) {
        byte result;
        do {
            result = (byte) generator.nextInt();
        } while (!Character.isLetterOrDigit(result));
        return result;
    }
}
