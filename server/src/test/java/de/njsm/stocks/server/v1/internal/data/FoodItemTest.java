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

import de.njsm.stocks.server.v1.internal.data.FoodItem;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.time.Instant;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class FoodItemTest {

    private int idReference;
    private Instant dateReference;
    private int ofTypeReference;
    private int storedInReference;
    private int registersReference;
    private int buysReference;

    private FoodItem uut;

    @Before
    public void setup() {
        idReference = 1;
        dateReference = Instant.now();
        ofTypeReference = 2;
        storedInReference = 3;
        registersReference = 4;
        buysReference = 5;

        uut = new FoodItem(idReference,
                dateReference,
                ofTypeReference,
                storedInReference,
                registersReference,
                buysReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.id);
        Assert.assertEquals(dateReference, uut.eatByDate);
        Assert.assertEquals(ofTypeReference, uut.ofType);
        Assert.assertEquals(storedInReference, uut.storedIn);
        Assert.assertEquals(registersReference, uut.registers);
        Assert.assertEquals(buysReference, uut.buys);
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
        String expectedStmt = "DELETE FROM \"Food_item\" WHERE \"ID\"=?";

        String actualStmt = uut.getRemoveStmt();

        Assert.assertEquals(expectedStmt, actualStmt);
    }

    @Test
    public void testVisitorCall() {
        StocksDataVisitor<Integer,Integer> input = Utils.getMockVisitor();
        Integer stub = 1;

        int result = uut.accept(input, stub);

        verify(input).foodItem(uut, stub);
        assertEquals(2, result);
    }

}
