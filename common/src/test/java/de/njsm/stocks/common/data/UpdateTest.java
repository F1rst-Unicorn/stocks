package de.njsm.stocks.common.data;

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class UpdateTest {

    Update uut;

    @Before
    public void setup() throws Exception {
        uut = new Update();
    }

    @Test
    public void testVisitorCall() {
        StocksDataVisitor<Integer,Integer> input = Utils.getMockVisitor();
        Integer stub = 1;

        int result = uut.accept(input, stub);

        verify(input).update(uut, stub);
        assertEquals(2, result);
    }


}