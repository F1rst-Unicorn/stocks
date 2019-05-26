/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.network.server;


import de.njsm.stocks.android.db.entities.*;
import de.njsm.stocks.android.frontend.device.ServerTicket;
import de.njsm.stocks.android.network.server.data.DataResponse;
import de.njsm.stocks.android.network.server.data.ListResponse;
import de.njsm.stocks.android.network.server.data.Response;
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

    @GET("/v2/location")
    Call<ListResponse<Location>> getLocations();

    @PUT("/v2/location")
    Call<Response> addLocation(@Query("name") String name);

    @PUT("/v2/location/rename")
    Call<Response> renameLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("new") String newName);

    @DELETE("/v2/location")
    Call<Response> deleteLocation(@Query("id") int id,
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
    Call<Response> deleteFood(@Query("id") int id,
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
    Call<Response> deleteFoodItem(@Query("id") int id,
                                  @Query("version") int version);

    @PUT("/v2/ean")
    Call<Response> addEanNumber(@Query("code") String code,
                                @Query("identifies") int identifies);

    @GET("/v2/ean")
    Call<ListResponse<EanNumber>> getEanNumbers();

    @DELETE("/v2/ean")
    Call<Response> deleteEanNumber(@Query("id") int id,
                                   @Query("version") int version);

}
