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
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.ScaledUnitRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class ScaledUnitViewModel extends ViewModel {

    private static final Logger LOG = new Logger(ScaledUnitViewModel.class);

    private LiveData<List<ScaledUnitView>> units;

    private final ScaledUnitRepository scaledUnitRepository;

    @Inject
    public ScaledUnitViewModel(ScaledUnitRepository scaledUnitRepository) {
        this.scaledUnitRepository = scaledUnitRepository;
    }

    public void init() {
        if (units == null) {
            LOG.d("Initialising");
            units = scaledUnitRepository.getUnits();
        }
    }

    public LiveData<ScaledUnitView> getUnit(int id) {
        return scaledUnitRepository.getUnit(id);
    }

    public LiveData<List<ScaledUnitView>> getUnits() {
        return units;
    }

    public LiveData<StatusCode> delete(ScaledUnit unit) {
        return scaledUnitRepository.delete(unit);
    }

    public LiveData<StatusCode> edit(ScaledUnit item, int unit, String scale) {
        return scaledUnitRepository.edit(item, unit, scale);
    }

    public LiveData<StatusCode> add(int unit, String scale) {
        return scaledUnitRepository.add(unit, scale);
    }
}
