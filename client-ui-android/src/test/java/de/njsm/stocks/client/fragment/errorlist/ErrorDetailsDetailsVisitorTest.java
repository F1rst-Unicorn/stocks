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

package de.njsm.stocks.client.fragment.errorlist;

import de.njsm.stocks.client.business.entities.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorDetailsDetailsVisitorTest {

    private ErrorDetailsDetailsVisitor uut;

    @Before
    public void setup() {
        uut = new ErrorDetailsDetailsVisitor();
    }

    @Test
    public void synchronsiationHasNoDetails() {
        SynchronisationErrorDetails input = SynchronisationErrorDetails.create();

        assertEquals("", uut.visit(input, null));
    }

    @Test
    public void locationAddShowsNameAndDescription() {
        LocationAddForm input = LocationAddForm.create("Fridge", "the cold one");

        assertEquals(input.name() + "\n" + input.description(), uut.visit(input, null));
    }

    @Test
    public void locationDeletionShowsName() {
        LocationDeleteErrorDetails data = LocationDeleteErrorDetails.create(1, "name");

        assertEquals(data.name(), uut.visit(data, null));
    }

    @Test
    public void locationEditingShowsNameAndDescription() {
        LocationEditErrorDetails data = LocationEditErrorDetails.create(1, "name", "description");
        assertEquals(data.name() + "\n" + data.description(), uut.visit(data, null));
    }

    @Test
    public void unitAddingShowsNameAndAbbreviation() {
        UnitAddForm data = UnitAddForm.create("Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }

    @Test
    public void unitEditingShowsNameAndAbbreviation() {
        UnitEditErrorDetails data = UnitEditErrorDetails.create(1, "Gramm", "g");
        assertEquals(data.name() + " (" + data.abbreviation() + ")", uut.visit(data, null));
    }
}
