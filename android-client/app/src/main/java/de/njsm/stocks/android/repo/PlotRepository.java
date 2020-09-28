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

package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import org.threeten.bp.ZoneId;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import de.njsm.stocks.android.db.dao.PlotDao;
import de.njsm.stocks.android.db.views.PlotPoint;
import de.njsm.stocks.android.util.Logger;

public class PlotRepository {

    private static final Logger LOG = new Logger(PlotRepository.class);

    private PlotDao plotDao;

    @Inject
    public PlotRepository(PlotDao plotDao) {
        this.plotDao = plotDao;
    }

    public LiveData<List<BarEntry>> getFoodHistogram(int foodId) {
        LOG.d("Getting food histogram of " + foodId);
        return plotDao.getExpirationHistogram(foodId);
    }

    public LiveData<List<Entry>> getFoodPlot(int foodId) {
        LOG.d("Getting food plot of " + foodId);

        MutableLiveData<List<Entry>> result = new MutableLiveData<>();
        plotDao.getFoodPlot(foodId).observeForever(l ->
                result.postValue(l.stream().map(new PlotPrefixSumComputer()).collect(Collectors.toList())));

        return result;
    }

    private static class PlotPrefixSumComputer implements Function<PlotPoint, Entry> {

        private int prefixSum;

        @Override
        public Entry apply(PlotPoint point) {
            prefixSum += point.getValue();
            float x = point.getTime().getEpochSecond() + ZoneId.systemDefault().getRules().getOffset(point.getTime()).getTotalSeconds();
            return new Entry(x, prefixSum);
        }
    }
}
