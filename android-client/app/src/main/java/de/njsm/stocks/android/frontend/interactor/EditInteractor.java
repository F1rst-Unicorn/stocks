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

package de.njsm.stocks.android.frontend.interactor;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

import java.util.function.BiFunction;

public abstract class EditInteractor<T, E> {

    private BiFunction<T, E, LiveData<StatusCode>> editor;

    protected BaseFragment owner;

    protected EditInteractor(BaseFragment owner,
                             BiFunction<T, E, LiveData<StatusCode>> editor) {
        this.editor = editor;
        this.owner = owner;
    }

    public void observeEditing(T item, E editedData) {
        LiveData<StatusCode> result = editor.apply(item, editedData);
        result.observe(owner, code -> treatErrorCode(code, item, editedData));
    }

    protected abstract void treatErrorCode(StatusCode code, T item, E editedData);

}
