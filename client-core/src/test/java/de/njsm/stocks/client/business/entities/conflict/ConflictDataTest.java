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

package de.njsm.stocks.client.business.entities.conflict;

import de.njsm.stocks.client.business.entities.UnitForSelection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConflictDataTest {

    @Test
    void ifNothingDiffersAnythingIsSuggested() {
        int data = 1;
        ConflictData<Integer> uut = ConflictData.create(data, data, data);
        assertEquals(data, uut.suggestedValue());
        assertFalse(uut.needsHandling());
    }

    @Test
    void locallyDifferentDataIsSuggested() {
        int data = 1;
        int differentData = 2;
        ConflictData<Integer> uut = ConflictData.create(data, data, differentData);
        assertEquals(differentData, uut.suggestedValue());
        assertFalse(uut.needsHandling());
    }

    @Test
    void remotelyDifferentDataIsSuggested() {
        int data = 1;
        int differentData = 2;
        ConflictData<Integer> uut = ConflictData.create(data, differentData, data);
        assertEquals(differentData, uut.suggestedValue());
        assertFalse(uut.needsHandling());
    }

    @Test
    void bothDifferingEquallyIsSuggested() {
        int data = 1;
        int differentData = 2;
        ConflictData<Integer> uut = ConflictData.create(data, differentData, differentData);
        assertEquals(differentData, uut.suggestedValue());
        assertFalse(uut.needsHandling());
    }

    @Test
    void inConflictLocalDataIsSuggested() {
        int data = 1;
        int differentData = 2;
        int localData = 3;
        ConflictData<Integer> uut = ConflictData.create(data, differentData, localData);
        assertEquals(localData, uut.suggestedValue());
        assertTrue(uut.needsHandling());
    }

    @Test
    void mergingDataWorks() {
        String original = "original";
        String remote = "remote";
        String local = "local";
        ConflictData<String> uut = ConflictData.createMerging(original, remote, local);
        assertEquals(
                "%s:\n" + original + "\n\n%s:\n" + remote + "\n\n%s:\n" + local,
                uut.suggestedValue()
        );
    }

    @Test
    void comparingByIdWorks() {
        UnitForSelection original = UnitForSelection.create(1, "1");
        UnitForSelection remote = UnitForSelection.create(1, "2");
        UnitForSelection local = UnitForSelection.create(1, "3");

        ConflictData<UnitForSelection> uut = ConflictData.create(original, remote, local, UnitForSelection::id);

        assertFalse(uut.needsHandling());
    }
}
