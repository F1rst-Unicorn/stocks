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

package de.njsm.stocks.client.fragment.errordetails;

import de.njsm.stocks.client.business.entities.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class ErrorDetailsPrinterTest {

    private ErrorDetailsFragment.ErrorDetailsPrinter uut;

    @Before
    public void setup() {
        uut = new ErrorDetailsFragment.ErrorDetailsPrinter();
    }

    @Test
    public void synchronisationErrorIsEmpty() {
        SynchronisationErrorDetails data = SynchronisationErrorDetails.create();
        assertEquals("", uut.visit(data, null));
    }

    @Test
    public void locationAddErrorShowsNameAndDescription() {
        LocationAddForm data = LocationAddForm.create("name", "description");
        assertEquals(data.name() + "\n" + data.description(), uut.visit(data, null));
    }

    @Test
    public void locationDeleteErrorShowsName() {
        LocationDeleteErrorDetails data = LocationDeleteErrorDetails.create(2, "name");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void locationEditErrorShowsName() {
        LocationEditErrorDetails data = LocationEditErrorDetails.create(2, "name", "description");
        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void unitAddErrorShowsName() {
        UnitAddForm data = UnitAddForm.create("Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void unitDeleteErrorShowsName() {
        UnitDeleteErrorDetails data = UnitDeleteErrorDetails.create(1, "Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void unitEditErrorShowsName() {
        UnitEditErrorDetails data = UnitEditErrorDetails.create(1, "Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void scaledUnitAddErrorShowsScaleAndUnit() {
        ScaledUnitAddErrorDetails data = ScaledUnitAddErrorDetails.create(BigDecimal.TEN.pow(3), 1, "Gramm", "g");
        assertEquals("1kg (Gramm)", uut.visit(data, null));
    }
}
