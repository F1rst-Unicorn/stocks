package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocationTest {

    private int idReference;
    private String nameReference;

    private Location uut;

    @Before
    public void setup() {
        idReference = 1;
        nameReference = "Fridge";

        uut = new Location(idReference,
                nameReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.id);
        Assert.assertEquals(nameReference, uut.name);
    }

    @Test
    public void testFillingAdd() throws SQLException {
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        uut.fillAddStmt(stmt);

        Mockito.verify(stmt).setString(1, nameReference);
        Mockito.verifyNoMoreInteractions(stmt);
    }

    @Test
    public void testGetAdd() {
        String expectedStmt = "INSERT INTO Location (name) VALUES (?)";

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
        String expectedStmt = "DELETE FROM Location WHERE ID=?";

        String actualStmt = uut.getRemoveStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
    }

}
