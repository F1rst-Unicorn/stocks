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

import androidx.lifecycle.LiveData;

public class ThreeWayJoiningLiveData<T1, T2, T3> extends LiveData<ThreeWayJoiningLiveData.Triple<T1, T2, T3>> {

    private Triple<T1, T2, T3> data;

    public ThreeWayJoiningLiveData(LiveData<T1> liveData1, LiveData<T2> liveData2, LiveData<T3> liveData3) {
        this.data = new Triple<>();
        liveData1.observeForever(this::updateT1);
        liveData2.observeForever(this::updateT2);
        liveData3.observeForever(this::updateT3);
    }

    private void updateT1(T1 t1) {
        this.data.t1 = t1;
        update();
    }

    private void updateT2(T2 t2) {
        this.data.t2 = t2;
        update();
    }

    private void updateT3(T3 t3) {
        this.data.t3 = t3;
        update();
    }

    private void update() {
        if (data.t1 != null && data.t2 != null && data.t3 != null)
            setValue(data);
    }

    public static class Triple<T1, T2, T3> {
        public T1 t1;
        public T2 t2;
        public T3 t3;

        public Triple() {}
    }
}
