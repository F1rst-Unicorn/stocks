package de.njsm.stocks.linux.client.network.server;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.*;
import retrofit.*;

import java.io.IOException;
import java.util.logging.Level;

public class ServerManager {

    protected ServerClient backend;

    public ServerManager(Configuration c) {
        try {
            String url = String.format("https://%s:%d/",
                    c.getServerName(),
                    c.getServerPort());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(c.getClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            backend = retrofit.create(ServerClient.class);
        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "Failed to set up ServerManager: " + e.getMessage());
        }
    }

    public Update[] getUpdates() {
        Call<Update[]> u = backend.getUpdates();

        try {
            Response<Update[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve updates: " + r.message());
            }
        } catch (IOException e) {
            error(e);
            return new Update[0];
        }
    }

    public User[] getUsers() {
        Call<User[]> u = backend.getUsers();

        try {
            Response<User[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve users: " + r.message());
            }
        } catch (IOException e) {
            error(e);
            return new User[0];
        }
    }

    public void addUser(User u) {
        Call<Void> call = backend.addUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw new RuntimeException("failed to create user: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeUser(User u) {
        Call<Void> call = backend.removeUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw new RuntimeException("failed to remove user: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public UserDevice[] getDevices() {
        Call<UserDevice[]> u = backend.getDevices();

        try {
            Response<UserDevice[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve devices: " + r.message());
            }
        } catch (IOException e) {
            error(e);
            return new UserDevice[0];
        }
    }

    public Ticket addDevice(UserDevice d) {
        Call<Ticket> call = backend.addDevice(d);

        try {
            Response<Ticket> r = call.execute();

            if (!r.isSuccess()) {
                throw new RuntimeException("failed to create device: " + r.message());
            } else {
                return r.body();
            }
        } catch (IOException e) {
            error(e);
        }
        return new Ticket();
    }

    public void removeDevice(UserDevice d) {
        Call<Void> call = backend.removeDevice(d);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                throw new RuntimeException("failed to remove device: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public Location[] getLocations() {
        Call<Location[]> u = backend.getLocations();

        try {
            Response<Location[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve locations: " + r.message());
            }
        } catch (IOException e) {
            error(e);
            return new Location[0];
        }
    }

    public void addLocation(Location l) {
        Call<Void> c = backend.addLocation(l);

        try {
            Response<Void> r = c.execute();

            if (! r.isSuccess()) {
                throw new RuntimeException("failed to add location: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeLocation(Location l) {
        Call<Void> c = backend.removeLocation(l);

        try {
            Response<Void> r = c.execute();

            if (! r.isSuccess()) {
                throw new RuntimeException("failed to remove location: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void renameLocation(Location l, String newName) {
        Call<Void> c = backend.renameLocation(l, newName);

        try {
            Response<Void> r = c.execute();

            if (!r.isSuccess()) {
                throw new RuntimeException("failed to rename location: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public Food[] getFood() {
        Call<Food[]> u = backend.getFood();

        try {
            Response<Food[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
            return new Food[0];
        }
    }

    public void addFood(Food f) {
        Call<Void> c = backend.addFood(f);

        try {
            Response<Void> r = c.execute();

            if (! r.isSuccess()) {
                throw new RuntimeException("failed to add food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeFood(Food f) {
        Call<Void> c = backend.removeFood(f);

        try {
            Response<Void> r = c.execute();

            if (! r.isSuccess()) {
                throw new RuntimeException("failed to remove food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void renameFood(Food f, String newName) {
        Call<Void> c = backend.renameFood(f, newName);

        try {
            Response<Void> r = c.execute();

            if (!r.isSuccess()) {
                throw new RuntimeException("failed to rename food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public FoodItem[] getFoodItems() {
        Call<FoodItem[]> u = backend.getFoodItems();

        try {
            Response<FoodItem[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve food items: " + r.message());
            }
        } catch (IOException e) {
            error(e);
            return new FoodItem[0];
        }
    }

    protected void error(IOException e) {
        System.out.println("No connection to server: " + e.getMessage());
    }

}
