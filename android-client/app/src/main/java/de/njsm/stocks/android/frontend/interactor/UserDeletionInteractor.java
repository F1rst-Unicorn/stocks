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