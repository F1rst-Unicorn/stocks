package de.njsm.stocks.linux.client.network.server;

import de.njsm.stocks.linux.client.data.*;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;


public interface ServerClient {

    @PUT("/user")
    void addUser(@Body User u);

    @GET("/user")
    Call<User[]> getUsers();

    @PUT("/user/remove")
    void removeUser(@Body User u);

    @PUT("/device")
    void addDevice(@Body UserDevice u);

    @GET("/device")
    Call<UserDevice[]> getDevices();

    @PUT("/device/remove")
    void removeDevice(@Body UserDevice u);

    @GET("/update")
    Call<Update[]> getUpdates();

    @GET("/location")
    Call<Location[]> getLocations();

    @PUT("/location")
    void addLocation(@Body Location l);

    @PUT("/location/{newname}")
    void renameLocation(@Body Location l, @Path("newname") String newName);

    @PUT("/location/remove")
    void removeLocation(@Body Location l);

    @GET("/food")
    Call<Food[]> getFood();

    @PUT("/food")
    void addFood(@Body Food f);

    @PUT("/food/{newname}")
    void renameFood(@Body Food f, @Path("newname") String newName);

    @PUT("/food/remove")
    void removeFood(@Body Food f);

    @GET("/food/fooditem")
    Call<FoodItem[]> getFoodItems();

    @PUT("/food/fooditem")
    void addFoodItem(@Body FoodItem f);

    @PUT("/food/fooditem/remove")
    void removeFoodItem(@Body FoodItem f);

}
