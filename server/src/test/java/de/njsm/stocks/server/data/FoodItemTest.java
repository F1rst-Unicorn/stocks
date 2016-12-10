package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class FoodItemTest {

    private int idReference;
    private Date dateReference;
    private int ofTypeReference;
    private int storedInReference;
    private int registersReference;
    private int buysReference;

    private FoodItem uut;

    @Before
    public void setup() {
        idReference = 1;
        dateReference = new Date();
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
    public void testFillingAdd() throws SQLException {
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        uut.fillAddStmt(stmt);

        Mockito.verify(stmt).setDate(1, new java.sql.Date(dateReference.getTime()));
        Mockito.verify(stmt).setInt(2, ofTypeReference);
        Mockito.verify(stmt).setInt(3, storedInReference);
        Mockito.verify(stmt).setInt(4, registersReference);
        Mockito.verify(stmt).setInt(5, buysReference);
        Mockito.verifyNoMoreInteractions(stmt);
    }

    @Test
    public void testGetAdd() {
        String expectedStmt = "INSERT INTO Food_item " +
                "(eat_by, of_type, stored_in, registers, buys) " +
                "VALUES (?,?,?,?,?)";

        String actualStmt = uut.getAddStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
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
}
