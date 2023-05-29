/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.network;

import de.njsm.stocks.common.api.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ServerApi {

    @GET("/v2/update")
    Call<ListResponse<Update>> getUpdates();

    @GET("/v2/location")
    Call<ListResponse<BitemporalLocation>> getLocations(@Query("bitemporal") int bitemporal,
                                                        @Query("startingFrom") String startingFrom);

    @DELETE("/v2/location")
    Call<Response> deleteLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("cascade") int cascade);

    @PUT("/v3/location")
    Call<DataResponse<Integer>> addLocation(@Body LocationForInsertion locationForInsertion);

    @PUT("/v3/location/edit")
    Call<Response> editLocation(@Body LocationForEditing location);

    @PUT("/v2/location/rename")
    Call<Response> renameLocation(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("new") String newName);

    @POST("/v2/location/description")
    @FormUrlEncoded
    Call<Response> setLocationDescription(@Query("id") int id,
                                          @Query("version") int version,
                                          @Field("description") String description);

    @GET("/v2/user")
    Call<ListResponse<BitemporalUser>> getUsers(@Query("bitemporal") int bitemporal,
                                                @Query("startingFrom") String startingFrom);

    @GET("/v2/device")
    Call<ListResponse<BitemporalUserDevice>> getUserDevices(@Query("bitemporal") int bitemporal,
                                                            @Query("startingFrom") String startingFrom);

    @GET("/v2/food")
    Call<ListResponse<BitemporalFood>> getFood(@Query("bitemporal") int bitemporal,
                                               @Query("startingFrom") String startingFrom);

    @GET("/v2/ean")
    Call<ListResponse<BitemporalEanNumber>> getEanNumbers(@Query("bitemporal") int bitemporal,
                                                          @Query("startingFrom") String startingFrom);

    @GET("/v2/fooditem")
    Call<ListResponse<BitemporalFoodItem>> getFoodItems(@Query("bitemporal") int bitemporal,
                                                        @Query("startingFrom") String startingFrom);

    @GET("/v2/unit")
    Call<ListResponse<BitemporalUnit>> getUnits(@Query("bitemporal") int bitemporal,
                                                @Query("startingFrom") String startingFrom);

    @GET("/v2/scaled-unit")
    Call<ListResponse<BitemporalScaledUnit>> getScaledUnits(@Query("bitemporal") int bitemporal,
                                                            @Query("startingFrom") String startingFrom);

    @GET("/v2/recipe")
    Call<ListResponse<BitemporalRecipe>> getRecipes(@Query("bitemporal") int bitemporal,
                                                    @Query("startingFrom") String startingFrom);

    @GET("/v2/recipe-ingredient")
    Call<ListResponse<BitemporalRecipeIngredient>> getRecipeIngredients(@Query("bitemporal") int bitemporal,
                                                                        @Query("startingFrom") String startingFrom);

    @GET("/v2/recipe-product")
    Call<ListResponse<BitemporalRecipeProduct>> getRecipeProducts(@Query("bitemporal") int bitemporal,
                                                                  @Query("startingFrom") String startingFrom);


    @PUT("/v2/unit")
    Call<Response> addUnit(@Query("name") String name,
                           @Query("abbreviation") String abbreviation);

    @DELETE("/v2/unit")
    Call<Response> deleteUnit(@Query("id") int id,
                              @Query("version") int version);

    @PUT("/v2/unit/rename")
    Call<Response> editUnit(@Query("id") int id,
                            @Query("version") int version,
                            @Query("name") String name,
                            @Query("abbreviation") String abbreviation);

    @PUT("/v2/scaled-unit")
    Call<Response> addScaledUnit(@Query("scale") String scale,
                                 @Query("unit") int unit);

    @PUT("/v2/scaled-unit/edit")
    Call<Response> editScaledUnit(@Query("id") int id,
                                  @Query("version") int version,
                                  @Query("scale") String scale,
                                  @Query("unit") int unit);

    @DELETE("/v2/scaled-unit")
    Call<Response> deleteScaledUnit(@Query("id") int id,
                                    @Query("version") int version);

    @PUT("/v3/food")
    Call<DataResponse<Integer>> addFood(@Body FoodForInsertion data);

    @DELETE("/v2/food")
    Call<Response> deleteFood(@Query("id") int id,
                              @Query("version") int version);

    @PUT("/v3/food/edit")
    Call<Response> editFood(@Body FoodForFullEditing food);

    @PUT("/v2/fooditem")
    Call<DataResponse<Integer>> addFoodItem(@Query("eatByDate") String date,
                                            @Query("storedIn") int storedIn,
                                            @Query("ofType") int ofType,
                                            @Query("unit") int unit);

    @DELETE("/v2/fooditem")
    Call<Response> deleteFoodItem(@Query("id") int id,
                                  @Query("version") int version);


    @PUT("/v2/fooditem/edit")
    Call<Response> editFoodItem(@Query("id") int id,
                                @Query("version") int version,
                                @Query("eatByDate") String eatBy,
                                @Query("storedIn") int storedIn,
                                @Query("unit") int unit);

    @PUT("/v2/ean")
    Call<DataResponse<Integer>> addEanNumber(@Query("code") String eanNumber,
                                             @Query("identifies") int identifies);

    @DELETE("/v2/ean")
    Call<Response> deleteEanNumber(@Query("id") int id,
                                   @Query("version") int version);

    @DELETE("/v2/device")
    Call<Response> deleteUserDevice(@Query("id") int id,
                                    @Query("version") int version);

    @DELETE("/v2/user")
    Call<Response> deleteUser(@Query("id") int id,
                              @Query("version") int version);

    @PUT("/v2/recipe")
    Call<DataResponse<Integer>> addRecipe(@Body FullRecipeForInsertion recipe);

    @PUT("/v2/food/buy")
    Call<Response> editFoodToBuy(@Query("id") int id,
                                 @Query("version") int version,
                                 @Query("buy") int toBuy);

    @PUT("/v2/user")
    Call<DataResponse<Integer>> addUser(@Query("name") String name);

    @PUT("/v2/device")
    Call<DataResponse<NewUserDeviceTicket>> addUserDevice(@Query("name") String name,
                                                          @Query("belongsTo") int id);

    @HTTP(method = "DELETE", path = "/v2/recipe", hasBody = true)
    Call<Response> deleteRecipe(@Body FullRecipeForDeletion recipe);
}
