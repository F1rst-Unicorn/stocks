package de.njsm.stocks.client.network.server;

import de.njsm.stocks.common.data.*;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;


public interface ServerClient {

    @PUT("/user")
    Call<Void> addUser(@Body User u);

    @GET("/user")
    Call<User[]> getUsers();

    @PUT("/user/remove")
    Call<Void> removeUser(@Body User u);

    @PUT("/device")
    Call<Ticket> addDevice(@Body UserDevice u);

    @GET("/device")
    Call<UserDevice[]> getDevices();

    @PUT("/device/remove")
    Call<Void> removeDevice(@Body UserDevice u);

    @GET("/update")
    Call<Update[]> getUpdates();

    @GET("/location")
    Call<Location[]> getLocations();

    @PUT("/location")
    Call<Void> addLocation(@Body Location l);

    @PUT("/location/{newname}")
    Call<Void> renameLocation(@Body Location l, @Path("newname") String newName);

    @PUT("/location/remove")
    Call<Void> removeLocation(@Body Location l);

    @GET("/food")
    Call<Food[]> getFood();

    @PUT("/food")
    Call<Void> addFood(@Body Food f);

    @PUT("/food/{newname}")
    Call<Void> renameFood(@Body Food f, @Path("newname") String newName);

    @PUT("/food/remove")
    Call<Void> removeFood(@Body Food f);

    @GET("/food/fooditem")
    Call<FoodItem[]> getFoodItems();

    @PUT("/food/fooditem")
    Call<Void> addFoodItem(@Body FoodItem f);

    @PUT("/food/fooditem/remove")
    Call<Void> removeFoodItem(@Body FoodItem f);

    @PUT("/food/fooditem/move/{newId}")
    Call<Void> moveFoodItem(@Body FoodItem f, @Path("newId") int newLoc);

}
