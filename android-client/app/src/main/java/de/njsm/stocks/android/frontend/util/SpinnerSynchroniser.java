/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.frontend.util;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.Data;
import de.njsm.stocks.android.util.Utility;
import de.njsm.stocks.android.util.livedata.JoiningLiveData;

import java.util.List;
import java.util.function.Consumer;

public class SpinnerSynchroniser<T extends Data> {

    JoiningLiveData<List<T>, Integer> liveData;

    public SpinnerSynchroniser(LifecycleOwner owner,
                               LiveData<List<T>> listLoader,
                               LiveData<Integer> preselectionLoader,
                               Consumer<Integer> callback) {
        liveData = new JoiningLiveData<>(owner, listLoader, preselectionLoader);
        liveData.observe(owner, pair -> {
            liveData.removeObservers(owner);
            Utility.find(pair.second, pair.first).ifPresent(callback::accept);
        });
    }
}
