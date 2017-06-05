package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.Utils;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.common.data.Update;
import de.njsm.stocks.common.data.visitor.DateToLocaltimeConverter;
import org.junit.Before;
import org.junit.Test;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerManagerTest {

    private ServerManager uut;

    private ServerClient client;

    private DataConverter converter;

    @Before
    public void setup() throws Exception {
        client = mock(ServerClient.class);
        converter = new DataConverter(Collections.singletonList(new DateToLocaltimeConverter()));
        uut = new ServerManager(client);
        uut.setConverter(converter);
    }

    @Test
    public void serverUpdatesAreConvertedToLocaltime() throws Exception {
        String date = "11.09.1991 00:00:00";
        Update[] input = new Update[1];
        input[0] = new Update("foo", new Date(
                Utils.getDate(date).getTime() +
                Utils.getTimezoneOffset()));
        Call<Update[]> call = createMockCall(input);
        when(client.getUpdates()).thenReturn(call);

        Update[] output = uut.getUpdates();

        assertEquals(1, output.length);
        assertEquals(Utils.getDate(date), output[0].lastUpdate);
    }

    @Test
    public void expirationDatesAreConvertedToLocaltime() throws Exception {
        String date = "11.09.1991 00:00:00";
        FoodItem[] input = new FoodItem[1];
        input[0] = new FoodItem(1, new Date(
                Utils.getDate(date).getTime() +
                Utils.getTimezoneOffset()), 0, 0, 0, 0);
        Call<FoodItem[]> call = createMockCall(input);
        when(client.getFoodItems()).thenReturn(call);

        FoodItem[] output = uut.getFoodItems();

        assertEquals(1, output.length);
        assertEquals(Utils.getDate(date), output[0].eatByDate);
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
            public void cancel() {

            }

            @Override
            public Call<Update[]> clone() {
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
            public void cancel() {

            }

            @Override
            public Call<FoodItem[]> clone() {
                return null;
            }
        };
    }
}
