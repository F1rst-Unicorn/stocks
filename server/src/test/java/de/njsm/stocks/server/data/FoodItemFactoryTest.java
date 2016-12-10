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

public class FoodItemFactoryTest {

    private int idReference;
    private Date dateReference;
    private int ofTypeReference;
    private int storedInReference;
    private int registersReference;
    private int buysReference;

    private int resultSetSize;

    private FoodItemFactory uut;
    private ResultSet rs;

    @Before
    public void setup() throws SQLException {
        idReference = 1;
        dateReference = new Date();
        ofTypeReference = 2;
        storedInReference = 3;
        registersReference = 4;
        buysReference = 5;
        resultSetSize = 3;

        rs = setupResultSet();
        uut = new FoodItemFactory();

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
        String expectedQuery = "SELECT * FROM Food_item";

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
        Mockito.when(rs.getDate("eat_by"))
                .thenReturn(new java.sql.Date(dateReference.getTime()));
        Mockito.when(rs.getInt("of_type")).thenReturn(ofTypeReference);
        Mockito.when(rs.getInt("stored_in")).thenReturn(storedInReference);
        Mockito.when(rs.getInt("registers")).thenReturn(registersReference);
        Mockito.when(rs.getInt("buys")).thenReturn(buysReference);
        return rs;
    }

    private void assertReferenceEquality(Data rawResult) {
        Assert.assertTrue(rawResult instanceof FoodItem);
        FoodItem result = (FoodItem) rawResult;
        Assert.assertEquals(idReference, result.id);
        Assert.assertEquals(dateReference, result.eatByDate);
        Assert.assertEquals(ofTypeReference, result.ofType);
        Assert.assertEquals(storedInReference, result.storedIn);
        Assert.assertEquals(registersReference, result.registers);
        Assert.assertEquals(buysReference, result.buys);
    }
}
