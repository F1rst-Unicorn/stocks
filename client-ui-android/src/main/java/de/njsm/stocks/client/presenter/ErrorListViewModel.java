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
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.ErrorListInteractor;
import de.njsm.stocks.client.business.ErrorRetryInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.ErrorDescription;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class ErrorListViewModel extends ViewModel {

    private final Synchroniser synchroniser;

    private final ErrorListInteractor errorListInteractor;

    private final ErrorRetryInteractor errorRetryInteractor;

    private final ObservableListCache<ErrorDescription> data;

    @Inject
    public ErrorListViewModel(ErrorListInteractor errorListInteractor, ErrorRetryInteractor errorRetryInteractor, Synchroniser synchroniser, ObservableListCache<ErrorDescription> data) {
        this.errorRetryInteractor = errorRetryInteractor;
        this.synchroniser = synchroniser;
        this.errorListInteractor = errorListInteractor;
        this.data = data;
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public LiveData<List<ErrorDescription>> getErrors() {
        return data.getLiveData(errorListInteractor::getErrors);
    }

    public void resolveDataByPosition(int listItemIndex, Consumer<ErrorDescription> callback) {
        data.performOnListItem(listItemIndex, callback::accept);
    }

    public void retry(ErrorDescription errorDescription) {
        errorRetryInteractor.retry(errorDescription);
    }

    public void delete(int listItemPosition) {
        data.performOnListItem(listItemPosition, errorRetryInteractor::delete);
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
