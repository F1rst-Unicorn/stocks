package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class UpdateFactoryTest {

    private String tableReference;
    private Date updateReference;

    private int resultSetSize;

    private UpdateFactory uut;
    private ResultSet rs;

    @Before
    public void setup() throws SQLException {
        tableReference = "Food_item";
        updateReference = new Date();

        resultSetSize = 3;

        rs = setupResultSet();
        uut = new UpdateFactory();

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
        String expectedQuery = "SELECT * FROM Updates";

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

        Mockito.when(rs.getString("table_name")).thenReturn(tableReference);
        Mockito.when(rs.getTimestamp("last_update")).thenReturn(new java.sql.Timestamp(updateReference.getTime()));
        return rs;
    }

    private void assertReferenceEquality(Data rawResult) {
        Assert.assertTrue(rawResult instanceof Update);
        Update result = (Update) rawResult;
        Assert.assertEquals(tableReference, result.table);
        Assert.assertEquals(updateReference, result.lastUpdate);
    }
}
