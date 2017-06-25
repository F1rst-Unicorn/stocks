package de.njsm.stocks.backend.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import de.njsm.stocks.Config;
import de.njsm.stocks.common.data.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Locale;

public class ServerManager {

    protected ServerClient backend;

    public static ServerManager m;

    public static void init(Context ctx) {
        if (m == null) {
            m = new ServerManager(ctx);
        }
    }

    public ServerManager(Context ctx) {
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(Config.preferences, Context.MODE_PRIVATE);
            String url = String.format(Locale.US, "https://%s:%d/",
                    prefs.getString(Config.serverNameConfig, ""),
                    prefs.getInt(Config.serverPortConfig, 0));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(Config.getClient(ctx))
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            backend = retrofit.create(ServerClient.class);
        } catch (Exception e) {
            Log.e(Config.log, "Failed to set up ServerManager: " + e.getMessage());
        }
    }

    public Update[] getUpdates() {
        Call<Update[]> call = backend.getUpdates();

        try {
            Response<Update[]> r = call.execute();

            if (r.isSuccessful()) {
                return r.body();
            } else {
                Log.e(Config.log, "failed to retrieve updates: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
        return new Update[0];
    }

    public User[] getUsers() {
        Call<User[]> call = backend.getUsers();

        try {
            Response<User[]> r = call.execute();

            if (r.isSuccessful()) {
                return r.body();
            } else {
                Log.e(Config.log, "failed to retrieve users: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
        return new User[0];
    }

    public void addUser(User u) {
        Call<Void> call = backend.addUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to create user: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeUser(User u) {
        Call<Void> call = backend.removeUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to remove user: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public UserDevice[] getDevices() {
        Call<UserDevice[]> u = backend.getDevices();

        try {
            Response<UserDevice[]> r = u.execute();

            if (r.isSuccessful()) {
                return r.body();
            } else {
                Log.e(Config.log, "failed to retrieve devices: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
        return new UserDevice[0];
    }

    public Ticket addDevice(UserDevice d) {
        Call<Ticket> call = backend.addDevice(d);

        try {
            Response<Ticket> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to create device: " + r.message());
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

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to remove device: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public Location[] getLocations() {
        Call<Location[]> u = backend.getLocations();

        try {
            Response<Location[]> r = u.execute();

            if (r.isSuccessful()) {
                return r.body();
            } else {
                Log.e(Config.log, "failed to retrieve locations: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
        return new Location[0];
    }

    public void addLocation(Location l) {
        Call<Void> call = backend.addLocation(l);

        try {
            Response<Void> r = call.execute();

            if (! r.isSuccessful()) {
                Log.e(Config.log, "failed to add location: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeLocation(Location l) {
        Call<Void> call = backend.removeLocation(l);

        try {
            Response<Void> r = call.execute();

            if (! r.isSuccessful()) {
                Log.e(Config.log, "failed to remove location: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void renameLocation(Location l, String newName) {
        Call<Void> call = backend.renameLocation(l, newName);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to rename location: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public Food[] getFood() {
        Call<Food[]> u = backend.getFood();

        try {
            Response<Food[]> r = u.execute();

            if (r.isSuccessful()) {
                return r.body();
            } else {
                Log.e(Config.log, "failed to retrieve food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
        return new Food[0];
    }

    public void addFood(Food f) {
        Call<Void> call = backend.addFood(f);

        try {
            Response<Void> r = call.execute();

            if (! r.isSuccessful()) {
                Log.e(Config.log, "failed to add food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeFood(Food f) {
        Call<Void> call = backend.removeFood(f);

        try {
            Response<Void> r = call.execute();

            if (! r.isSuccessful()) {
                Log.e(Config.log, "failed to remove food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void renameFood(Food f, String newName) {
        Call<Void> call = backend.renameFood(f, newName);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to rename food: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public FoodItem[] getFoodItems() {
        Call<FoodItem[]> u = backend.getFoodItems();

        try {
            Response<FoodItem[]> r = u.execute();

            if (r.isSuccessful()) {
                return r.body();
            } else {
                Log.e(Config.log, "failed to retrieve food items: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
        return new FoodItem[0];
    }

    public void addItem(FoodItem f) {
        Call<Void> call = backend.addFoodItem(f);

        try {
            Response<Void> r = call.execute();

            if (! r.isSuccessful()) {
                Log.e(Config.log, "failed to add food item: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeItem(FoodItem f) {
        Call<Void> call = backend.removeFoodItem(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to remove item: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void move(FoodItem f, int newLoc) {
        Call<Void> call = backend.moveFoodItem(f, newLoc);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccessful()) {
                Log.e(Config.log, "failed to move item: " + r.message());
            }
        } catch (IOException e) {
            error(e);
        }
    }

    protected void error(IOException e) {
        Log.e(Config.log, "No connection to server: " + e.getMessage());
    }

}
