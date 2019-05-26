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

package de.njsm.stocks.server.v1.internal.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import java.time.Instant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UpdateFactoryTest {

    private String tableReference;
    private Instant updateReference;

    private int resultSetSize;

    private UpdateFactory uut;
    private ResultSet rs;

    @Before
    public void setup() throws SQLException {
        tableReference = "Food_item";
        updateReference = Instant.now();

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
        List<Update> resultList = uut.createDataList(rs);

        Assert.assertEquals(resultSetSize, resultList.size());
        for (Data rawResult : resultList) {
            assertReferenceEquality(rawResult);
        }
    }

    @Test
    public void testGetQuery() {
        String expectedQuery = "SELECT * FROM \"Updates\"";

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
        Mockito.when(rs.getTimestamp("last_update")).thenReturn(new java.sql.Timestamp(updateReference.toEpochMilli()));
        return rs;
    }

    private void assertReferenceEquality(Data rawResult) {
        Assert.assertTrue(rawResult instanceof Update);
        Update result = (Update) rawResult;
        Assert.assertEquals(tableReference, result.table);
        Assert.assertEquals(updateReference, result.lastUpdate);
    }
}
