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
import de.njsm.stocks.server.v2.db.TicketBackend;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Stream;

public class DeviceManager extends BusinessObject {

    private static final Logger LOG = LogManager.getLogger(DeviceManager.class);

    private static final int TICKET_LENGTH = 64;

    private UserDeviceHandler deviceBackend;

    private FoodItemHandler foodItemHandler;

    private TicketBackend ticketBackend;

    private AuthAdmin authAdmin;

    public DeviceManager(UserDeviceHandler deviceBackend,
                         FoodItemHandler foodItemHandler,
                         TicketBackend ticketBackend,
                         AuthAdmin authAdmin) {
        super(deviceBackend);
        this.deviceBackend = deviceBackend;
        this.foodItemHandler = foodItemHandler;
        this.ticketBackend = ticketBackend;
        this.authAdmin = authAdmin;
    }

    public Validation<StatusCode, NewDeviceTicket> addDevice(UserDevice device) {
        return runFunction(() -> {

            Validation<StatusCode, Integer> deviceAddResult = deviceBackend.add(device);
            if (deviceAddResult.isFail())
                return Validation.fail(deviceAddResult.fail());

            device.id = deviceAddResult.success();
            String ticket = generateTicket();

            StatusCode ticketAddResult = ticketBackend.addTicket(device, ticket);
            if (ticketAddResult != StatusCode.SUCCESS)
                return Validation.fail(ticketAddResult);

            NewDeviceTicket result = new NewDeviceTicket(deviceAddResult.success(), ticket);
            return Validation.success(result);
        });
    }

    public Validation<StatusCode, Stream<UserDevice>> get(AsyncResponse r) {
        return runFunction(r, () -> {
            deviceBackend.setReadOnly();
            return deviceBackend.get();
        });
    }

    public StatusCode removeDevice(UserDevice device, Principals currentUser) {
        return runOperation(() -> removeDeviceInternally(device, currentUser));
    }

    public StatusCode revokeDevice(UserDevice device) {
        return runOperation(() -> {
            deviceBackend.setReadOnly();
            return authAdmin.revokeCertificate(device.id);
        });
    }

    Validation<StatusCode, List<UserDevice>> getDevicesBelonging(User u) {
        return deviceBackend.getDevicesOfUser(u);
    }

    StatusCode removeDeviceInternally(UserDevice device, Principals currentUser) {

        StatusCode transferResult = foodItemHandler.transferFoodItems(device, currentUser.toDevice());
        if (transferResult != StatusCode.SUCCESS)
            return transferResult;

        StatusCode deleteResult = deviceBackend.delete(device);
        if (deleteResult != StatusCode.SUCCESS)
            return deleteResult;

        return authAdmin.revokeCertificate(device.id);
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
