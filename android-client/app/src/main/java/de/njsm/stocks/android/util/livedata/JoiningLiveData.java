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

package de.njsm.stocks.android.util.livedata;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

public class JoiningLiveData<T1, T2> extends LiveData<Pair<T1, T2>> {

    private T1 t1;

    private T2 t2;

    public JoiningLiveData(LiveData<T1> liveData1, LiveData<T2> liveData2) {
        liveData1.observeForever(this::updateT1);
        liveData2.observeForever(this::updateT2);
    }

    public JoiningLiveData(LifecycleOwner owner, LiveData<T1> liveData1, LiveData<T2> liveData2) {
        liveData1.observe(owner, this::updateT1);
        liveData2.observe(owner, this::updateT2);
    }

    private void updateT1(T1 t1) {
        this.t1 = t1;
        update();
    }

    private void updateT2(T2 t2) {
        this.t2 = t2;
        update();
    }

    private void update() {
        if (t1 != null && t2 != null)
            setValue(new Pair<>(t1, t2));
    }
}
