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

public abstract class DeletionInteractor<T> {

    private static final Logger LOG = new Logger(DeletionInteractor.class);

    private Function<T, LiveData<StatusCode>> deleter;

    private Consumer<T> deletionCancler;

    protected BaseFragment owner;

    private View snackbarParent;

    protected DeletionInteractor(BaseFragment owner,
                                 View snackbarParent,
                                 Consumer<T> deletionCancler,
                                 Function<T, LiveData<StatusCode>> deleter) {
        this.deleter = deleter;
        this.deletionCancler = deletionCancler;
        this.owner = owner;
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

    protected void observeDeletion(T item) {
        LiveData<StatusCode> result = deleter.apply(item);
        result.observe(owner, code -> treatErrorCode(code, item));
    }

    protected abstract int getSnackbarMessageId();

    protected abstract void treatErrorCode(StatusCode code, T item);

}
