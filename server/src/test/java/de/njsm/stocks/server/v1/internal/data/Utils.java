package de.njsm.stocks.server.v1.internal.data;

import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class Utils {

    public static StocksDataVisitor<Integer, Integer> getMockVisitor() {
        StocksDataVisitor<Integer,Integer> result = Mockito.mock(StocksDataVisitor.class);
        when(result.food(any(), any())).thenReturn(2);
        when(result.foodItem(any(), any())).thenReturn(2);
        when(result.location(any(), any())).thenReturn(2);
        when(result.user(any(), any())).thenReturn(2);
        when(result.userDevice(any(), any())).thenReturn(2);
        when(result.serverTicket(any(), any())).thenReturn(2);
        when(result.ticket(any(), any())).thenReturn(2);
        when(result.update(any(), any())).thenReturn(2);
        when(result.eanNumber(any(), any())).thenReturn(2);
        return result;
    }


}
