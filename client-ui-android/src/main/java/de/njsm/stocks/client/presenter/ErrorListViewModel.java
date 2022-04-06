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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.ErrorListInteractor;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread;

public class ErrorListViewModel extends ViewModel {

    private final Synchroniser synchroniser;

    private final ErrorListInteractor errorListInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private Observable<List<ErrorDescription>> data;

    @Inject
    public ErrorListViewModel(ErrorListInteractor errorListInteractor, ErrorRetryInteractor errorRetryInteractor, Synchroniser synchroniser) {
        this.errorRetryInteractor = errorRetryInteractor;
        this.synchroniser = synchroniser;
        this.errorListInteractor = errorListInteractor;
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public LiveData<List<ErrorDescription>> getErrors() {
        return LiveDataReactiveStreams.fromPublisher(
                getData().toFlowable(BackpressureStrategy.LATEST));
    }

    public void resolveId(int listItemIndex, Consumer<Long> callback) {
        performOnCurrentData(list -> callback.accept(list.get(listItemIndex).id()));
    }

    public void retry(int listItemIndex) {
        performOnCurrentData(list -> errorRetryInteractor.retry(list.get(listItemIndex)));
    }

    public void delete(int listItemPosition) {
        performOnCurrentData(list -> errorRetryInteractor.delete(list.get(listItemPosition)));
    }

    private void performOnCurrentData(Consumer<List<ErrorDescription>> consumer) {
        getData()
                .firstElement()
                .observeOn(mainThread())
                .subscribe(consumer::accept);
    }

    private Observable<List<ErrorDescription>> getData() {
        if (data == null)
            data = errorListInteractor.getErrors();
        return data;
    }
}
