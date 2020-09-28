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

package de.njsm.stocks.android.frontend.foodcharts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

import de.njsm.stocks.android.repo.PlotRepository;

public class PlotViewModel extends ViewModel {

    private PlotRepository plotViewModel;

    private LiveData<List<Entry>> data;

    private LiveData<List<BarEntry>> histogramData;

    public PlotViewModel(PlotRepository plotViewModel) {
        this.plotViewModel = plotViewModel;
    }

    public void init(int id) {
        if (data == null) {
            data = plotViewModel.getFoodPlot(id);
            histogramData = plotViewModel.getFoodHistogram(id);
        }
    }

    public LiveData<List<Entry>> getHistory() {
        return data;
    }

    public LiveData<List<BarEntry>> getHistogramData() {
        return histogramData;
    }
}
