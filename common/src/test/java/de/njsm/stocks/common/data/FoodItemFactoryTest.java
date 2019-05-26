/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.common.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.threeten.bp.Instant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FoodItemFactoryTest {

    private int idReference;
    private Instant dateReference;
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
        dateReference = Instant.now();
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
        List<FoodItem> resultList = uut.createDataList(rs);

        Assert.assertEquals(resultSetSize, resultList.size());
        for (Data rawResult : resultList) {
            assertReferenceEquality(rawResult);
        }
    }

    @Test
    public void testGetQuery() {
        String expectedQuery = "SELECT * FROM Food_item ORDER BY eat_by, ID";

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
        Mockito.when(rs.getTimestamp("eat_by"))
                .thenReturn(new java.sql.Timestamp(dateReference.toEpochMilli()));
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
