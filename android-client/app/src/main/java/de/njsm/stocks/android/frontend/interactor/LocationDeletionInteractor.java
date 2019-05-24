package de.njsm.stocks.android.frontend.interactor;

import android.view.View;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class LocationDeletionInteractor extends DeletionInteractor<Location> {

    private Function<Integer, LiveData<Location>> updater;

    private Function<Location, LiveData<StatusCode>> cascadingDeleter;

    public LocationDeletionInteractor(BaseFragment owner,
                                      View snackbarParent,
                                      Consumer<Location> deletionCancler,
                                      Function<Location, LiveData<StatusCode>> deleter,
                                      Function<Integer, LiveData<Location>> updater,
                                      Function<Location, LiveData<StatusCode>> cascadingDeleter) {
        super(owner, snackbarParent, deletionCancler, deleter);
        this.updater = updater;
        this.cascadingDeleter = cascadingDeleter;
    }

    @Override
    protected int getSnackbarMessageId() {
        return R.string.dialog_location_was_deleted;
    }

    @Override
    protected void treatErrorCode(StatusCode code, Location item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Location> newData = updater.apply(item.id);
            newData.observe(owner, newLocation -> {
                if (newLocation != null && ! newLocation.equals(item)) {
                    compareLocations(item, newLocation);
                    newData.removeObservers(owner);
                }
            });
        } else if (code == StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION)
            offerCascadingDeletion(item);
        else
            owner.maybeShowDeleteError(code);
    }

    private void compareLocations(Location item, Location upstreamItem) {
        String message = owner.requireContext().getString(R.string.error_location_changed, item.name, upstreamItem.name);
        owner.showErrorDialog(R.string.title_delete_location, message, (d, w) -> observeDeletion(upstreamItem));
    }

    private void offerCascadingDeletion(Location item) {
        String message = owner.requireContext().getString(R.string.error_location_foreign_key_violation, item.name);
        owner.showErrorDialog(R.string.title_delete_location, message, (d, w) -> {
            LiveData<StatusCode> result = cascadingDeleter.apply(item);
            result.observe(owner, c -> treatErrorCode(c, item));
        });
    }
}
