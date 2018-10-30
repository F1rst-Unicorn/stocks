package de.njsm.stocks.server.v1.internal.data;

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

    private EanNumberFactory uut;
    private ResultSet rs;

    @Before
    public void setup() throws SQLException {
        idReference = 1;
        codeReference = "123-123-123";
        identifiesReference = 3;
        resultSetSize = 3;

        rs = setupResultSet();
        uut = new EanNumberFactory();

    }

    @Test
    public void testSingleCreation() throws SQLException {
        Data rawResult = uut.createData(rs);

        assertReferenceEquality(rawResult);
    }

    @Test
    public void testBulkCreation() throws SQLException {
        List<Data> resultList = uut.createDataList(rs);

        Assert.assertEquals(resultSetSize, resultList.size());
        for (Data rawResult : resultList) {
            assertReferenceEquality(rawResult);
        }
    }

    @Test
    public void testGetQuery() {
        String expectedQuery = "SELECT * FROM \"EAN_number\"";

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
                return callCounter <= resultSetSize;
            }
        };
        Mockito.when(rs.next()).thenAnswer(a);

        Mockito.when(rs.getInt("ID")).thenReturn(idReference);
        Mockito.when(rs.getString("number")).thenReturn(codeReference);
        Mockito.when(rs.getInt("identifies")).thenReturn(identifiesReference);
        return rs;
    }

    private void assertReferenceEquality(Data rawResult) {
        Assert.assertTrue(rawResult instanceof EanNumber);
        EanNumber result = (EanNumber) rawResult;
        Assert.assertEquals(idReference, result.id);
        Assert.assertEquals(codeReference, result.eanCode);
        Assert.assertEquals(identifiesReference, result.identifiesFood);
    }
}
