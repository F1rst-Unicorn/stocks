package de.njsm.stocks.android.frontend.interactor;

import android.view.View;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class DeviceDeletionInteractor extends DeletionInteractor<UserDevice> {

    public DeviceDeletionInteractor(BaseFragment owner,
                                    View snackbarParent,
                                    Consumer<UserDevice> deletionCancler,
                                    Function<UserDevice, LiveData<StatusCode>> deleter) {
        super(owner, snackbarParent, deletionCancler, deleter);
    }

    @Override
    protected int getSnackbarMessageId() {
        return R.string.dialog_device_was_deleted;
    }

    @Override
    protected void treatErrorCode(StatusCode code, UserDevice item) {
        owner.maybeShowDeleteError(code);
    }
}