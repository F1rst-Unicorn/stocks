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
}
