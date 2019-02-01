package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.UserHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserManager extends BusinessObject {

    private static final Logger LOG = LogManager.getLogger(UserManager.class);

    private UserHandler dbHandler;

    private DeviceManager deviceManager;

    private FoodItemHandler foodItemHandler;

    public UserManager(UserHandler dbHandler,
                       DeviceManager deviceManager,
                       FoodItemHandler foodItemHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.deviceManager = deviceManager;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode addUser(User u) {
        return runOperation(() -> dbHandler.add(u)
                .toEither().left().orValue(StatusCode.SUCCESS));
    }

    public Validation<StatusCode, List<User>> get() {
        return runFunction(() -> {
            dbHandler.setReadOnly();
            return dbHandler.get();
        });
    }

    public StatusCode deleteUser(User userToDelete, Principals currentUser) {
        return runOperation(() -> {
            Validation<StatusCode, List<UserDevice>> devices = deviceManager.getDevicesBelonging(userToDelete);

            if (devices.isFail())
                return devices.fail();

            for (UserDevice device : devices.success()) {
                StatusCode removeCode = deviceManager.removeDeviceInternally(device, currentUser);

                if (removeCode != StatusCode.SUCCESS) {
                    return removeCode;
                }
            }
            StatusCode transferItemsCode = foodItemHandler.transferFoodItems(userToDelete, currentUser.toUser());

            if (transferItemsCode != StatusCode.SUCCESS) {
                return transferItemsCode;
            }

            return dbHandler.delete(userToDelete);
        });
    }

}
