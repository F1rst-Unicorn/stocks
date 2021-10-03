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
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.repo.UnitRepository;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class UnitViewModel extends ViewModel {

    private static final Logger LOG = new Logger(UnitViewModel.class);

    private LiveData<List<Unit>> units;

    private final UnitRepository unitRepository;

    @Inject
    public UnitViewModel(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public void init() {
        if (units == null) {
            LOG.d("Initialising");
            units = unitRepository.getUnits();
        }
    }

    public LiveData<List<Unit>> getUnits() {
        return units;
    }

    public LiveData<StatusCode> delete(Unit unit) {
        return unitRepository.delete(unit);
    }

    public LiveData<StatusCode> edit(Unit item, String newName, String newAbbreviation) {
        return unitRepository.edit(item, newName, newAbbreviation);
    }

    public LiveData<StatusCode> add(String name, String abbreviation) {
        return unitRepository.add(name, abbreviation);
    }
}
