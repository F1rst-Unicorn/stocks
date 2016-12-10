package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EanNumberFactoryTest {

    private int idReference;
    private String codeReference;
    private int identifiesReference;

    private int resultSetSize;

    @Before
    public void setup() {
        idReference = 1;
        codeReference = "123-123-123";
        identifiesReference = 3;
        resultSetSize = 3;
    }

    @Test
    public void testSingleCreation() throws SQLException {
        ResultSet rs = setupResultSet();
        EanNumberFactory uut = new EanNumberFactory();

        Data rawResult = uut.createData(rs);

        Assert.assertTrue(rawResult instanceof EanNumber);
        EanNumber result = (EanNumber) rawResult;
        Assert.assertEquals(idReference, result.id);
        Assert.assertEquals(codeReference, result.eanCode);
        Assert.assertEquals(identifiesReference, result.identifiesFood);
    }


    @Test
    public void testBulkCreation() throws SQLException {
        ResultSet rs = setupResultSet();
        EanNumberFactory uut = new EanNumberFactory();

        List<Data> resultList = uut.createDataList(rs);

        for (Data rawResult : resultList) {
            Assert.assertTrue(rawResult instanceof EanNumber);
            EanNumber result = (EanNumber) rawResult;
            Assert.assertEquals(idReference, result.id);
            Assert.assertEquals(codeReference, result.eanCode);
            Assert.assertEquals(identifiesReference, result.identifiesFood);
        }
    }

    @Test
    public void testGetQuery() {
        EanNumberFactory uut = new EanNumberFactory();
        String expectedQuery = "SELECT * FROM EAN_number";
        String actualQuery = uut.getQuery();

        Assert.assertEquals(expectedQuery, actualQuery);
    }

    private ResultSet setupResultSet() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        Answer<Boolean> a = new Answer<Boolean>() {
            private int callCounter;
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                callCounter++;
                return callCounter >= resultSetSize;
            }
        };
        Mockito.when(rs.next()).thenAnswer(a);
        Mockito.when(rs.getInt("ID")).thenReturn(idReference);
        Mockito.when(rs.getString("number")).thenReturn(codeReference);
        Mockito.when(rs.getInt("identifies")).thenReturn(identifiesReference);
        return rs;
    }
}
