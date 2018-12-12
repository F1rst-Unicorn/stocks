package de.njsm.stocks.server.v1.internal.data;

import de.njsm.stocks.server.v1.internal.data.Food;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class FoodTest {

    private int idReference;
    private String nameReference;
    private String newNameReference;

    private Food uut;

    @Before
    public void setup() {
        idReference = 1;
        nameReference = "Carrot";
        newNameReference = "Beer";

        uut = new Food(idReference,
                nameReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.id);
        Assert.assertEquals(nameReference, uut.name);
    }

    @Test
    public void testGetRename() {
        String expectedStmt = "UPDATE \"Food\" SET \"name\"=? WHERE \"ID\"=?";

        String actualStmt = uut.getRenameStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
    }

    @Test
    public void testFillingRename() throws SQLException {
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        uut.fillRenameStmt(stmt, newNameReference);

        Mockito.verify(stmt).setString(1, newNameReference);
        Mockito.verify(stmt).setInt(2, idReference);
        Mockito.verifyNoMoreInteractions(stmt);
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
        String expectedStmt = "DELETE FROM \"Food\" WHERE \"ID\"=?";

        String actualStmt = uut.getRemoveStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
    }

    @Test
    public void testToString() {
        String expectedString = "Food (" + idReference + ", " + nameReference + ")";

        String actualString = uut.toString();

        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void testVisitorCall() {
        StocksDataVisitor<Integer,Integer> input = Utils.getMockVisitor();
        Integer stub = 1;

        int result = uut.accept(input, stub);

        verify(input).food(uut, stub);
        assertEquals(2, result);
    }


}