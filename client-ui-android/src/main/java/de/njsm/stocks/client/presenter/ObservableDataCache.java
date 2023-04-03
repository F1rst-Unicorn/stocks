/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

class ObservableDataCache<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ObservableDataCache.class);

    private Observable<T> data;

    private BehaviorSubject<T> dataForLaterGetting;

    @Inject
    ObservableDataCache() {
    }

    LiveData<T> getLiveData(Supplier<Observable<T>> supplier) {
        return LiveDataReactiveStreams.fromPublisher(getFlowable(supplier));
    }

    Flowable<T> getFlowable(Supplier<Observable<T>> supplier) {
        return getData(supplier).toFlowable(BackpressureStrategy.LATEST);
    }

    Observable<T> getData(Supplier<Observable<T>> supplier) {
        if (data == null) {
            dataForLaterGetting = BehaviorSubject.create();
            data = supplier.get();
            var unused = data.subscribe(dataForLaterGetting::onNext, dataForLaterGetting::onError);
        }
        return data;
    }

    void performOnCurrentData(Consumer<? super T> consumer) {
        if (dataForLaterGetting == null) {
            LOG.error("cache not initialised, ignoring current action");
            return;
        }

        var unused = dataForLaterGetting.firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    <E> void performOnNestedList(int listItemIndex, Function<T, List<E>> mapper, Consumer<? super E> consumer) {
        performOnCurrentData(v -> ObservableListCache.performOnResolvedList(listItemIndex, consumer, mapper.apply(v)));
    }
}
