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

package de.njsm.stocks.android.frontend.eannumber;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.android.repo.EanNumberRepository;

import java.util.List;

public class EanNumberViewModel extends ViewModel {

    private LiveData<List<EanNumber>> data;

    private EanNumberRepository repository;

    public EanNumberViewModel(EanNumberRepository repository) {
        this.repository = repository;
    }

    public void init(int foodId) {
        if (data == null) {
            data = repository.getEanCodesOf(foodId);
        }
    }

    public LiveData<List<EanNumber>> getData() {
        return data;
    }

    public LiveData<StatusCode> addEanNumber(String code, int identifies) {
        return repository.addEanCode(code, identifies);
    }

    public LiveData<StatusCode> deleteEanNumber(EanNumber number) {
        return repository.deleteEanCode(number);
    }
}
