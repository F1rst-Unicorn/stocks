package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.common.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.Call;
import retrofit.Response;

import java.io.IOException;

public class ServerManager {

    private static final Logger LOG = LogManager.getLogger(ServerManager.class);

    private ServerClient backend;

    public ServerManager(ServerClient backend) {
        this.backend = backend;
    }

    public Update[] getUpdates() throws NetworkException {
        Call<Update[]> call = backend.getUpdates();

        try {
            Response<Update[]> r = call.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw error(r, "Error getting updates");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public User[] getUsers() throws NetworkException {
        Call<User[]> call = backend.getUsers();

        try {
            Response<User[]> r = call.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw error(r, "Error getting users");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server");
        }
    }

    public void addUser(User u) throws NetworkException {
        Call<Void> call = backend.addUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error adding user");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void removeUser(User u) throws NetworkException {
        Call<Void> call = backend.removeUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error removing user");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public UserDevice[] getDevices() throws NetworkException {
        Call<UserDevice[]> u = backend.getDevices();

        try {
            Response<UserDevice[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw error(r, "Error getting devices");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public Ticket addDevice(UserDevice d) throws NetworkException {
        Call<Ticket> call = backend.addDevice(d);

        try {
            Response<Ticket> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error adding device");
            } else {
                return r.body();
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void removeDevice(UserDevice d) throws NetworkException {
        Call<Void> call = backend.removeDevice(d);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error removing device");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public Location[] getLocations() throws NetworkException {
        Call<Location[]> u = backend.getLocations();

        try {
            Response<Location[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw error(r, "Error getting locations");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void addLocation(Location l) throws NetworkException {
        Call<Void> call = backend.addLocation(l);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error adding location");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void removeLocation(Location l) throws NetworkException {
        Call<Void> call = backend.removeLocation(l);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error removing location");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void renameLocation(Location l, String newName) throws NetworkException {
        Call<Void> call = backend.renameLocation(l, newName);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error renaming location");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public Food[] getFood() throws NetworkException {
        Call<Food[]> u = backend.getFood();

        try {
            Response<Food[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw error(r, "Error getting food");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void addFood(Food f) throws NetworkException {
        Call<Void> call = backend.addFood(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error adding food");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void removeFood(Food f) throws NetworkException {
        Call<Void> call = backend.removeFood(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error removing food");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void renameFood(Food f, String newName) throws NetworkException {
        Call<Void> call = backend.renameFood(f, newName);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error renaming food");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public FoodItem[] getFoodItems() throws NetworkException {
        Call<FoodItem[]> u = backend.getFoodItems();

        try {
            Response<FoodItem[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw error(r, "Error getting food items");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void addItem(FoodItem f) throws NetworkException {
        Call<Void> call = backend.addFoodItem(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error adding food item");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void removeItem(FoodItem f) throws NetworkException {
        Call<Void> call = backend.removeFoodItem(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error removing food item");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    public void move(FoodItem f, int newLoc) throws NetworkException {
        Call<Void> call = backend.moveFoodItem(f, newLoc);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw error(r, "Error moving food item");
            }
        } catch (IOException e) {
            throw new NetworkException("Error connecting to the server", e);
        }
    }

    private NetworkException error(Response<?> r, String message) throws IOException {
        LOG.error(message + ". Response was:\n" +
                r.errorBody().string());
        return new NetworkException("Error getting updates");
    }
}
