package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EanNumberTest {

    private int idReference;
    private String codeReference;
    private int identifiesReference;

    private EanNumber uut;

    @Before
    public void setup() {
        idReference = 1;
        codeReference = "123-123-123";
        identifiesReference = 3;

        uut = new EanNumber(idReference,
                codeReference,
                identifiesReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.id);
        Assert.assertEquals(codeReference, uut.eanCode);
        Assert.assertEquals(identifiesReference, uut.identifiesFood);
    }

    @Test
    public void testFillingAdd() throws SQLException {
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        uut.fillAddStmt(stmt);

        Mockito.verify(stmt).setString(1, codeReference);
        Mockito.verify(stmt).setInt(2, identifiesReference);
        Mockito.verifyNoMoreInteractions(stmt);
    }

    @Test
    public void testGetAdd() {
        String expectedStmt = "INSERT INTO EAN_number (number, identifies) VALUES (?,?)";

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
        String expectedStmt = "DELETE FROM EAN_number WHERE ID=?";

        String actualStmt = uut.getRemoveStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
    }

}
