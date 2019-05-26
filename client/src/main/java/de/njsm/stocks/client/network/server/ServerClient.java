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

package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.exceptions.NetworkException;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;


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
                                  @Query("new") String newName) throws NetworkException;

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
                              @Query("new") String newName) throws NetworkException;

    @DELETE("/v2/food")
    Call<Response> removeFood(@Query("id") int id,
                              @Query("version") int version) throws NetworkException;


    @GET("/v2/fooditem")
    Call<ListResponse<FoodItem>> getFoodItems() throws NetworkException;

    @PUT("/v2/fooditem")
    Call<Response> addFoodItem(@Query("eatByDate") String eatByDate,
                               @Query("storedIn") int storedIn,
                               @Query("ofType") int ofType) throws NetworkException;

    @PUT("/v2/fooditem/edit")
    Call<Response> editFoodItem(@Query("id") int id,
                                @Query("version") int version,
                                @Query("eatByDate") String eatByDate,
                                @Query("storedIn") int storedIn) throws NetworkException;

    @DELETE("/v2/fooditem")
    Call<Response> removeFoodItem(@Query("id") int id,
                                  @Query("version") int version) throws NetworkException;
}
