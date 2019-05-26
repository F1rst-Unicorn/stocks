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

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

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
        assertEquals(idReference, uut.id);
        assertEquals(codeReference, uut.eanCode);
        assertEquals(identifiesReference, uut.identifiesFood);
    }

    @Test
    public void testFillingRemove() throws SQLException {
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        uut.fillRemoveStmt(stmt);

        verify(stmt).setInt(1, idReference);
        Mockito.verifyNoMoreInteractions(stmt);
    }

    @Test
    public void testGetRemove() {
        String expectedStmt = "DELETE FROM EAN_number WHERE ID=?";

        String actualStmt = uut.getRemoveStmt();

        assertEquals(expectedStmt, actualStmt);
    }

    @Test
    public void testVisitorCall() {
        StocksDataVisitor<Integer,Integer> input = Utils.getMockVisitor();
        Integer stub = 1;

        int result = uut.accept(input, stub);

        verify(input).eanNumber(uut, stub);
        assertEquals(2, result);
    }
}
