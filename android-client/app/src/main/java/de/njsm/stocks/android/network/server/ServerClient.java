package de.njsm.stocks.android.network.server;


import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.network.server.data.DataResponse;
import de.njsm.stocks.android.network.server.data.ListResponse;
import de.njsm.stocks.android.network.server.data.Response;
import de.njsm.stocks.android.frontend.device.ServerTicket;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ServerClient {

    @PUT("/v2/user")
    Call<Response> addUser(@Query("name") String name);

    @GET("/v2/user")
    Call<ListResponse<User>> getUsers();

    @DELETE("/v2/user")
    Call<Response> deleteUser(@Query("id") int id,
                              @Query("version") int version);

    @PUT("/v2/device")
    Call<DataResponse<ServerTicket>> addDevice(@Query("name") String name,
                                               @Query("belongsTo") int uid);

    @GET("/v2/device")
    Call<ListResponse<UserDevice>> getDevices();

    @DELETE("/v2/device")
    Call<Response> deleteDevice(@Query("id") int id,
                                @Query("version") int version);

    @GET("/v2/update")
    Call<ListResponse<Update>> getUpdates();
/*

    @GET("/v2/location")
    Call<ListResponse<Location>> getLocations();

    @PUT("/v2/location")
    Call<Response> addLocation(@Query("name") String name);

    @PUT("/v2/location/rename")
    Call<Response> renameLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("new") String newName);

    @DELETE("/v2/location")
    Call<Response> removeLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("cascade") int cascade);


    @GET("/v2/food")
    Call<ListResponse<Food>> getFood();

    @PUT("/v2/food")
    Call<Response> addFood(@Query("name") String name);

    @PUT("/v2/food/rename")
    Call<Response> renameFood(@Query("id") int id,
                              @Query("version") int version,
                              @Query("new") String newName);

    @DELETE("/v2/food")
    Call<Response> removeFood(@Query("id") int id,
                              @Query("version") int version);


    @GET("/v2/fooditem")
    Call<ListResponse<FoodItem>> getFoodItems();

    @PUT("/v2/fooditem")
    Call<Response> addFoodItem(@Query("eatByDate") String eatByDate,
                               @Query("storedIn") int storedIn,
                               @Query("ofType") int ofType);

    @PUT("/v2/fooditem/edit")
    Call<Response> editFoodItem(@Query("id") int id,
                                @Query("version") int version,
                                @Query("eatByDate") String eatByDate,
                                @Query("storedIn") int storedIn);

    @DELETE("/v2/fooditem")
    Call<Response> removeFoodItem(@Query("id") int id,
                                  @Query("version") int version);

    @PUT("/v2/ean")
    Call<Response> addEanNumber(@Query("code") String code,
                                @Query("identifies") int identifies);

    @GET("/v2/ean")
    Call<ListResponse<EanNumber>> getEanNumbers();

    @DELETE("/v2/ean")
    Call<Response> deleteEanNumber(@Query("id") int id,
                                   @Query("version") int version);*/

}
