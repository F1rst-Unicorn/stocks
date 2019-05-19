package de.njsm.stocks.android.frontend.interactor;

import android.view.View;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class EanNumberDeletionInteractor extends DeletionInteractor<EanNumber> {

    public EanNumberDeletionInteractor(BaseFragment owner,
                                       View snackbarParent,
                                       Consumer<EanNumber> deletionCancler,
                                       Function<EanNumber, LiveData<StatusCode>> deleter) {
        super(owner, snackbarParent, deletionCancler, deleter);
    }

    @Override
    protected int getSnackbarMessageId() {
        return R.string.dialog_ean_was_deleted;
    }

    @Override
    protected void treatErrorCode(StatusCode code, EanNumber item) {
        owner.maybeShowDeleteError(code);
    }
}
