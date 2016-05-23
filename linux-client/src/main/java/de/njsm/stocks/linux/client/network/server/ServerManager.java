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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
        }
    }

}
