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

import de.njsm.stocks.client.business.StatusCode;
import de.njsm.stocks.client.business.data.FoodItem;
import de.njsm.stocks.client.business.data.Update;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerManagerTest {

    private ServerManager uut;

    private ServerClient client;

    @Before
    public void setup() throws Exception {
        client = mock(ServerClient.class);
        uut = new ServerManager(client);
    }

    @Test
    public void serverUpdatesAreTakenAsIs() throws Exception {
        Update[] input = new Update[1];
        input[0] = new Update("foo", Instant.ofEpochMilli(0));
        Call<ListResponse<Update>> call = createMockCall(input);
        when(client.getUpdates()).thenReturn(call);

        List<Update> output = uut.getUpdates();

        assertEquals(1, output.size());
        assertEquals(0, output.get(0).lastUpdate.toEpochMilli());
    }

    @Test
    public void expirationDatesAreConvertedToLocaltime() throws Exception {
        FoodItem[] input = new FoodItem[1];
        input[0] = new FoodItem(1, 6, Instant.ofEpochMilli(0), 0, 0, 0, 0);
        Call<ListResponse<FoodItem>> call = createMockCall(input);
        when(client.getFoodItems()).thenReturn(call);

        List<FoodItem> output = uut.getFoodItems();

        assertEquals(1, output.size());
        assertEquals(0, output.get(0).eatByDate.toEpochMilli());
    }

    private <T> Call<ListResponse<T>> createMockCall(T[] input) {
        return new Call<ListResponse<T>>() {
            @Override
            public Response<ListResponse<T>> execute() throws IOException {
                return Response.success(new ListResponse<T>(StatusCode.SUCCESS, input));
            }

            @Override
            public void enqueue(Callback<ListResponse<T>> callback) {

            }

            @Override
            public boolean isExecuted() {
                return true;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<ListResponse<T>> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }
}
