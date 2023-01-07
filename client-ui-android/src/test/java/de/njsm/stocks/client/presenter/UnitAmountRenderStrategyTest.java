/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.presenter;

import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class UnitAmountRenderStrategyTest {

    private UnitAmountRenderStrategy uut = new UnitAmountRenderStrategy();

    private Locale backup;

    @Before
    public void setFixedLocale() {
        backup = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);
    }

    @After
    public void tearDown() {
        Locale.setDefault(backup);
    }

    @Test
    public void plainUnitIsAppended() {
        assertEquals("5g", uut.render(ScaledUnitForListing.create(1, "g", BigDecimal.valueOf(5))));
    }

    @Test
    public void unitWithDecimalPointRespectsLocale() {
        assertEquals("5,5g", uut.render(ScaledUnitForListing.create(1, "g", BigDecimal.valueOf(5.5))));
    }

    @Test
    public void scalePrefixIsInserted() {
        assertEquals("5kg", uut.render(ScaledUnitForListing.create(1, "g", BigDecimal.valueOf(5000))));
    }

    @Test
    public void upperCaseUnitInsertsSpace() {
        assertEquals("5 Bottle", uut.render(ScaledUnitForListing.create(1, "Bottle", BigDecimal.valueOf(5))));
    }

    @Test
    public void upperCaseUnitWithPrefixInsertsSpace() {
        assertEquals("5 k Bottle", uut.render(ScaledUnitForListing.create(1, "Bottle", BigDecimal.valueOf(5000))));
    }

    @Test
    public void listIsRenderedByJoining() {
        assertEquals("5kg, 1piece", uut.render(asList(
                ScaledUnitForListing.create(1, "g", BigDecimal.valueOf(5000)),
                ScaledUnitForListing.create(1, "piece", BigDecimal.valueOf(1))
        )));
    }
}
