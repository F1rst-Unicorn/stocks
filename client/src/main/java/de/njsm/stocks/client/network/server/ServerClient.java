package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.exceptions.NetworkException;
import retrofit2.Call;
import retrofit2.http.*;

import java.time.Instant;


public interface ServerClient {

    @PUT("/v2/user")
    Call<Response> addUser(@Query("name") String name) throws NetworkException;

    @GET("/v2/user")
    Call<ListResponse<User>> getUsers() throws NetworkException;

    @DELETE("/v2/user")
    Call<Response> removeUser(@Query("id") int id,
                              @Query("version") int version) throws NetworkException;


    @PUT("/v2/device")
    Call<DataResponse<ServerTicket>> addDevice(@Query("name") String name,
                                               @Query("belongsTo") int uid) throws NetworkException;

    @GET("/v2/device")
    Call<ListResponse<UserDevice>> getDevices() throws NetworkException;

    @DELETE("/v2/device")
    Call<Response> removeDevice(@Query("id") int id,
                                @Query("version") int version) throws NetworkException;



    @GET("/v2/update")
    Call<ListResponse<Update>> getUpdates() throws NetworkException;


    @GET("/v2/location")
    Call<ListResponse<Location>> getLocations() throws NetworkException;

    @PUT("/v2/location")
    Call<Response> addLocation(@Query("name") String name) throws NetworkException;

    @PUT("/v2/location/rename")
    Call<Response> renameLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("newName") String newName) throws NetworkException;

    @DELETE("/v2/location")
    Call<Response> removeLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("cascade") int cascade) throws NetworkException;


    @GET("/v2/food")
    Call<ListResponse<Food>> getFood() throws NetworkException;

    @PUT("/v2/food")
    Call<Response> addFood(@Query("name") String name) throws NetworkException;

    @PUT("/v2/food/rename")
    Call<Response> renameFood(@Query("id") int id,
                              @Query("version") int version,
                              @Query("newName") String newName) throws NetworkException;

    @DELETE("/v2/food")
    Call<Response> removeFood(@Query("id") int id,
                              @Query("version") int version) throws NetworkException;


    @GET("/v2/fooditem")
    Call<ListResponse<FoodItem>> getFoodItems() throws NetworkException;

    @PUT("/v2/fooditem")
    Call<Response> addFoodItem(@Query("eatByDate") Instant eatByDate,
                               @Query("storedIn") int storedIn,
                               @Query("ofType") int ofType) throws NetworkException;

    @PUT("/v2/fooditem/edit")
    Call<Response> editFoodItem(@Query("id") int id,
                                @Query("version") int version,
                                @Query("eatByDate") Instant eatByDate,
                                @Query("storedIn") int storedIn) throws NetworkException;

    @DELETE("/v2/fooditem")
    Call<Response> removeFoodItem(@Query("id") int id,
                                  @Query("version") int version) throws NetworkException;
}
