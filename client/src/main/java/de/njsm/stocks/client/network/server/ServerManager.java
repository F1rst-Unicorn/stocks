package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.StatusCode;
import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.json.InstantSerialiser;
import de.njsm.stocks.client.exceptions.NetworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class ServerManager {

    private static final Logger LOG = LogManager.getLogger(ServerManager.class);

    private ServerClient backend;

    public ServerManager(ServerClient backend) {
        this.backend = backend;
    }

    public List<Update> getUpdates() throws NetworkException {
        Call<ListResponse<Update>> call = backend.getUpdates();
        return executeQuery(call);
    }

    public List<User> getUsers() throws NetworkException {
        Call<ListResponse<User>> call = backend.getUsers();
        return executeQuery(call);
    }

    public void addUser(User u) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.addUser(u.name);
        executeCommand(call);
    }

    public void removeUser(User u) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.removeUser(u.id, u.version);
        executeCommand(call);
    }

    public List<UserDevice> getDevices() throws NetworkException {
        Call<ListResponse<UserDevice>> u = backend.getDevices();
        return executeQuery(u);
    }

    public ServerTicket addDevice(UserDevice d) throws NetworkException {
        Call<DataResponse<ServerTicket>> call = backend.addDevice(d.name, d.userId);
        return executeScalarQuery(call);
    }

    public void removeDevice(UserDevice d) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.removeDevice(d.id, d.version);
        executeCommand(call);
    }

    public List<Location> getLocations() throws NetworkException {
        Call<ListResponse<Location>> call = backend.getLocations();
        return executeQuery(call);
    }

    public void addLocation(Location l) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.addLocation(l.name);
        executeCommand(call);
    }

    public void removeLocation(Location l, boolean cascade) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.removeLocation(l.id, l.version, cascade ? 1 : 0);
        executeCommand(call);
    }

    public void renameLocation(Location l, String newName) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.renameLocation(l.id, l.version, newName);
        executeCommand(call);
    }

    public List<Food> getFood() throws NetworkException {
        Call<ListResponse<Food>> call = backend.getFood();
        return executeQuery(call);
    }

    public void addFood(Food f) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.addFood(f.name);
        executeCommand(call);
    }

    public void removeFood(Food f) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.removeFood(f.id, f.version);
        executeCommand(call);
    }

    public void renameFood(Food f, String newName) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.renameFood(f.id, f.version, newName);
        executeCommand(call);
    }

    public List<FoodItem> getFoodItems() throws NetworkException {
        Call<ListResponse<FoodItem>> call = backend.getFoodItems();
        return executeQuery(call);
    }

    public void addItem(FoodItem f) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.addFoodItem(InstantSerialiser.FORMAT.format(f.eatByDate), f.storedIn, f.ofType);
        executeCommand(call);
    }

    public void removeItem(FoodItem f) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.removeFoodItem(f.id, f.version);
        executeCommand(call);
    }

    public void edit(FoodItem f, Instant eatBy, int newLoc) throws NetworkException {
        Call<de.njsm.stocks.client.network.server.Response> call = backend.editFoodItem(f.id, f.version, InstantSerialiser.FORMAT.format(eatBy), newLoc);
        executeCommand(call);
    }

    private <T> List<T> executeQuery(Call<ListResponse<T>> call) throws NetworkException {
        try {
            Response<ListResponse<T>> r = call.execute();
            return Arrays.asList(returnResponse(r));
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    private <T> T executeScalarQuery(Call<DataResponse<T>> call) throws NetworkException {
        try {
            Response<DataResponse<T>> r = call.execute();
            return returnResponse(r);
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    private void executeCommand(Call<de.njsm.stocks.client.network.server.Response> call) throws NetworkException {
        try {
            Response<de.njsm.stocks.client.network.server.Response> r = call.execute();
            handleResponse(r);
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    private <T extends de.njsm.stocks.client.network.server.Response> void handleResponse(Response<T> response) throws NetworkException {
        if (!response.isSuccessful()
                || response.body() == null
                || response.body().status != StatusCode.SUCCESS)
            throw error(response);
    }

    private <D> D returnResponse(Response<? extends DataResponse<D>> response) throws NetworkException {
        if (!response.isSuccessful()
                || response.body() == null
                || response.body().status != StatusCode.SUCCESS)
            throw error(response);
        else
            return response.body().data;
    }

    private NetworkException error(Response<? extends de.njsm.stocks.client.network.server.Response> r) {
        logResponse(r);
        de.njsm.stocks.client.network.server.Response response = r.body();

        if (response != null) {
            switch (response.status) {
                case INVALID_DATA_VERSION:
                    return new NetworkException("This object has been changed by another user.\nPlease check again.");
                case NOT_FOUND:
                    return new NetworkException("This object has been deleted by another user.");
                case INVALID_ARGUMENT:
                    return new NetworkException("You sent an invalid request to the server.");
                case CA_UNREACHABLE:
                case DATABASE_UNREACHABLE:
                    return new NetworkException("The server is in trouble.\nPlease notify an admin.");
                case FOREIGN_KEY_CONSTRAINT_VIOLATION:
                    return new NetworkException("Other objects are still linked with this one.\n" +
                            "E.g. A location cannot be deleted as long as it contains food items.\n" +
                            "Make sure to delete or move them first.");
                case SUCCESS:
                case SERIALISATION_CONFLICT:
                case ACCESS_DENIED:
                    return new NetworkException("This error should not happen here.");
                default:
                    return new NetworkException("There is a network problem.");
            }
        } else {
            return new NetworkException("There is a network problem, the server didn't reply.");
        }
    }

    private <T> void logResponse(Response<T> r) {
        if (r.errorBody() != null) {
            try {
                LOG.error("Response was an error:\n" +
                        r.errorBody().string());
            } catch (IOException e) {
                LOG.error("Response was an error and the body returned an exception");
            }
        }
        else
            LOG.error("Response was an error without body");
    }
}
