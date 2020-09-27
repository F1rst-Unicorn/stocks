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

import com.github.mikephil.charting.data.Entry;

import org.threeten.bp.ZoneId;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public LiveData<List<Entry>> getFoodPlot(int foodId) {
        LOG.d("Getting food plot of " + foodId);

        MutableLiveData<List<Entry>> result = new MutableLiveData<>();
        plotDao.getFoodPlot(foodId).observeForever(l -> {
            Iterator<PlotPoint> iterator = l.iterator();
            PlotPrefixSumComputer spliterator = new PlotPrefixSumComputer(iterator);
            result.postValue(StreamSupport.stream(spliterator, false).collect(Collectors.toList()));
        });

        return result;
    }

    private static class PlotPrefixSumComputer implements Spliterator<Entry> {

        private Iterator<PlotPoint> iterator;

        private int prefixSum;

        private PlotPrefixSumComputer(Iterator<PlotPoint> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Entry> action) {
            if (iterator.hasNext()) {
                PlotPoint point = iterator.next();
                prefixSum += point.getValue();
                float x = point.getTime().getEpochSecond() + ZoneId.systemDefault().getRules().getOffset(point.getTime()).getTotalSeconds();
                Entry result = new Entry(x, prefixSum);
                action.accept(result);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Spliterator<Entry> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return iterator.hasNext() ? Long.MAX_VALUE : 0;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL;
        }
    }

}
