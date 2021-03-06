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

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

public abstract class SnackbarDeletionInteractor<T> extends DeletionInteractor<T> {

    private static final Logger LOG = new Logger(SnackbarDeletionInteractor.class);

    private final Consumer<T> deletionCancler;

    private final View snackbarParent;

    public SnackbarDeletionInteractor(BaseFragment owner, Function<T, LiveData<StatusCode>> deleter, Consumer<T> deletionCancler, View snackbarParent) {
        super(owner, deleter);
        this.deletionCancler = deletionCancler;
        this.snackbarParent = snackbarParent;
    }

    public void initiateDeletion(T item) {
        showDeletionSnackbar(item);
    }

    protected void showDeletionSnackbar(T item) {
        Snackbar.make(snackbarParent, getSnackbarMessageId(), Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, v -> {})
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        switch (event) {
                            case DISMISS_EVENT_ACTION:
                                LOG.d("Deletion cancelled");
                                deletionCancler.accept(item);
                                break;
                            case DISMISS_EVENT_CONSECUTIVE:
                            case DISMISS_EVENT_MANUAL:
                            case DISMISS_EVENT_SWIPE:
                            case DISMISS_EVENT_TIMEOUT:
                                observeDeletion(item);
                                break;
                        }
                    }
                })
                .show();
    }

    protected abstract int getSnackbarMessageId();
}
