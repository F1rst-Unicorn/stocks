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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.NewDeviceTicket;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.TicketHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class DeviceManager extends BusinessObject {

    private static final Logger LOG = LogManager.getLogger(DeviceManager.class);

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

    public Validation<StatusCode, NewDeviceTicket> addDevice(UserDevice device) {
        return runFunction(() -> {

            Validation<StatusCode, Integer> deviceAddResult = userDeviceHandler.add(device);
            if (deviceAddResult.isFail())
                return Validation.fail(deviceAddResult.fail());

            device.id = deviceAddResult.success();
            String ticket = generateTicket();

            StatusCode ticketAddResult = ticketHandler.addTicket(device, ticket);
            if (ticketAddResult != StatusCode.SUCCESS)
                return Validation.fail(ticketAddResult);

            NewDeviceTicket result = new NewDeviceTicket(deviceAddResult.success(), ticket);
            return Validation.success(result);
        });
    }

    public Validation<StatusCode, Stream<UserDevice>> get(AsyncResponse r, boolean bitemporal, Instant startingFrom) {
        return runAsynchronously(r, () -> {
            userDeviceHandler.setReadOnly();
            return userDeviceHandler.get(bitemporal, startingFrom);
        });
    }

    public StatusCode removeDevice(UserDevice device) {
        return runOperation(() -> removeDeviceInternally(device));
    }

    public StatusCode revokeDevice(UserDevice device) {
        return runOperation(() -> {
            userDeviceHandler.setReadOnly();
            return authAdmin.revokeCertificate(device.id);
        });
    }

    Validation<StatusCode, List<UserDevice>> getDevicesBelonging(User u) {
        return userDeviceHandler.getDevicesOfUser(u);
    }

    StatusCode removeDeviceInternally(UserDevice device) {
        return foodItemHandler.transferFoodItems(device, principals.toDevice())
                .bind(() -> ticketHandler.removeTicketOfDevice(device))
                .bind(() -> userDeviceHandler.delete(device))
                .bind(() -> authAdmin.revokeCertificate(device.id));
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

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        foodItemHandler.setPrincipals(principals);
        ticketHandler.setPrincipals(principals);
    }
}
