package de.njsm.stocks.client.network.server;

import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Update;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

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
        Call<Update[]> call = createMockCall(input);
        when(client.getUpdates()).thenReturn(call);

        Update[] output = uut.getUpdates();

        assertEquals(1, output.length);
        assertEquals(0, output[0].lastUpdate.toEpochMilli());
    }

    @Test
    public void expirationDatesAreConvertedToLocaltime() throws Exception {
        FoodItem[] input = new FoodItem[1];
        input[0] = new FoodItem(1, Instant.ofEpochMilli(0), 0, 0, 0, 0);
        Call<FoodItem[]> call = createMockCall(input);
        when(client.getFoodItems()).thenReturn(call);

        FoodItem[] output = uut.getFoodItems();

        assertEquals(1, output.length);
        assertEquals(0, output[0].eatByDate.toEpochMilli());
    }

    private Call<Update[]> createMockCall(Update[] input) {
        return new Call<Update[]>() {
            @Override
            public Response<Update[]> execute() throws IOException {
                return Response.success(input);
            }

            @Override
            public void enqueue(Callback<Update[]> callback) {

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
            public Call<Update[]> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }

    private Call<FoodItem[]> createMockCall(FoodItem[] input) {
        return new Call<FoodItem[]>() {
            @Override
            public Response<FoodItem[]> execute() throws IOException {
                return Response.success(input);
            }

            @Override
            public void enqueue(Callback<FoodItem[]> callback) {

            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<FoodItem[]> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }
}
