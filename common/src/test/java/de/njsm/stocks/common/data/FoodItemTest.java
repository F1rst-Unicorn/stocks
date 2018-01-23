package de.njsm.stocks.common.data;

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class FoodItemTest {

    private int idReference;
    private Instant dateReference;
    private int ofTypeReference;
    private int storedInReference;
    private int registersReference;
    private int buysReference;

    private FoodItem uut;

    @Before
    public void setup() {
        idReference = 1;
        dateReference = Instant.now();
        ofTypeReference = 2;
        storedInReference = 3;
        registersReference = 4;
        buysReference = 5;

        uut = new FoodItem(idReference,
                dateReference,
                ofTypeReference,
                storedInReference,
                registersReference,
                buysReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.id);
        Assert.assertEquals(dateReference, uut.eatByDate);
        Assert.assertEquals(ofTypeReference, uut.ofType);
        Assert.assertEquals(storedInReference, uut.storedIn);
        Assert.assertEquals(registersReference, uut.registers);
        Assert.assertEquals(buysReference, uut.buys);
    }

    @Test
    public void testFillingRemove() throws SQLException {
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        uut.fillRemoveStmt(stmt);

        Mockito.verify(stmt).setInt(1, idReference);
        Mockito.verifyNoMoreInteractions(stmt);
    }

    @Test
    public void testGetRemove() {
        String expectedStmt = "DELETE FROM Food_item WHERE ID=?";

        String actualStmt = uut.getRemoveStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
    }

    @Test
    public void testVisitorCall() {
        StocksDataVisitor<Integer,Integer> input = Utils.getMockVisitor();
        Integer stub = 1;

        int result = uut.accept(input, stub);

        verify(input).foodItem(uut, stub);
        assertEquals(2, result);
    }

}
