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

import android.view.View;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class UserDeletionInteractor extends DeletionInteractor<User> {

    public UserDeletionInteractor(BaseFragment owner,
                                  View snackbarParent,
                                  Consumer<User> deletionCancler,
                                  Function<User, LiveData<StatusCode>> deleter) {
        super(owner, snackbarParent, deletionCancler, deleter);
    }

    @Override
    protected int getSnackbarMessageId() {
        return R.string.dialog_user_was_deleted;
    }

    @Override
    protected void treatErrorCode(StatusCode code, User item) {
        owner.maybeShowDeleteError(code);
    }
}