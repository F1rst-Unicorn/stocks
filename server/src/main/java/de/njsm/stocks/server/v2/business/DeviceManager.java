package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.TicketBackend;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.List;

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

    public Validation<StatusCode, ClientTicket> addDevice(UserDevice device) {
        return runFunction(() -> {

            Validation<StatusCode, Integer> deviceAddResult = deviceBackend.add(device);
            if (deviceAddResult.isFail())
                return Validation.fail(deviceAddResult.fail());

            device.id = deviceAddResult.success();
            String ticket = generateTicket();

            StatusCode ticketAddResult = ticketBackend.addTicket(device, ticket);
            if (ticketAddResult != StatusCode.SUCCESS)
                return Validation.fail(ticketAddResult);

            ClientTicket result = new ClientTicket(deviceAddResult.success(), ticket);

            return Validation.success(result);
        });
    }

    public Validation<StatusCode, List<UserDevice>> get() {
        return runFunction(() -> {
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
