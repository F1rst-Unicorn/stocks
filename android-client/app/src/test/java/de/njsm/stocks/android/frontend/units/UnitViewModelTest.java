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

package de.njsm.stocks.android.frontend.units;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.repo.UnitRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UnitViewModelTest {

    private UnitViewModel uut;

    private UnitRepository repo;

    @Before
    public void setup() {
        repo = Mockito.mock(UnitRepository.class);
        uut = new UnitViewModel(repo);
    }

    @Test
    public void uninitialisedViewModelReturnsNull() {
        assertNull(uut.getUnits());
    }

    @Test
    public void initialisedViewModelReturnsData() {
        MutableLiveData<List<Unit>> data = new MutableLiveData<>();
        Mockito.when(repo.getUnits()).thenReturn(data);

        uut.init();
        LiveData<List<Unit>> result = uut.getUnits();

        assertEquals(data, result);
        Mockito.verify(repo).getUnits();
    }
}
