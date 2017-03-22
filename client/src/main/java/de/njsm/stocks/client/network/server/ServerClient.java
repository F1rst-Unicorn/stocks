package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.common.data.*;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;


public interface ServerClient {

    @PUT("/user")
    Call<Void> addUser(@Body User u) throws NetworkException;

    @GET("/user")
    Call<User[]> getUsers() throws NetworkException;

    @PUT("/user/remove")
    Call<Void> removeUser(@Body User u) throws NetworkException;

    @PUT("/device")
    Call<Ticket> addDevice(@Body UserDevice u) throws NetworkException;

    @GET("/device")
    Call<UserDevice[]> getDevices() throws NetworkException;

    @PUT("/device/remove")
    Call<Void> removeDevice(@Body UserDevice u) throws NetworkException;

    @GET("/update")
    Call<Update[]> getUpdates() throws NetworkException;

    @GET("/location")
    Call<Location[]> getLocations() throws NetworkException;

    @PUT("/location")
    Call<Void> addLocation(@Body Location l) throws NetworkException;

    @PUT("/location/{newname}")
    Call<Void> renameLocation(@Body Location l, @Path("newname") String newName) throws NetworkException;

    @PUT("/location/remove")
    Call<Void> removeLocation(@Body Location l) throws NetworkException;

    @GET("/food")
    Call<Food[]> getFood() throws NetworkException;

    @PUT("/food")
    Call<Void> addFood(@Body Food f) throws NetworkException;

    @PUT("/food/{newname}")
    Call<Void> renameFood(@Body Food f, @Path("newname") String newName) throws NetworkException;

    @PUT("/food/remove")
    Call<Void> removeFood(@Body Food f) throws NetworkException;

    @GET("/food/fooditem")
    Call<FoodItem[]> getFoodItems() throws NetworkException;

    @PUT("/food/fooditem")
    Call<Void> addFoodItem(@Body FoodItem f) throws NetworkException;

    @PUT("/food/fooditem/remove")
    Call<Void> removeFoodItem(@Body FoodItem f) throws NetworkException;

    @PUT("/food/fooditem/move/{newId}")
    Call<Void> moveFoodItem(@Body FoodItem f, @Path("newId") int newLoc) throws NetworkException;

}
