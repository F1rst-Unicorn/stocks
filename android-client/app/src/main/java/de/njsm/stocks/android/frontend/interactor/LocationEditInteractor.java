package de.njsm.stocks.android.frontend.interactor;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

import java.util.function.BiFunction;

public class LocationEditInteractor extends EditInteractor<Location, String> {

    private Function<Integer, LiveData<Location>> updater;

    public LocationEditInteractor(BaseFragment owner,
                                  BiFunction<Location, String, LiveData<StatusCode>> editor,
                                  Function<Integer, LiveData<Location>> updater) {
        super(owner, editor);
        this.updater = updater;
    }

    @Override
    protected void treatErrorCode(StatusCode code, Location item, String editedData) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Location> newData = updater.apply(item.id);
            newData.observe(owner, newItem -> {
                if (newItem != null && !newItem.equals(item)) {
                    compareLocation(item, editedData, newItem);
                    newData.removeObservers(owner);
                }
            });
        } else
            owner.maybeShowEditError(code);
    }

    private void compareLocation(Location item, String localNewName, Location upstreamItem) {
        String message = owner.getString(R.string.error_location_changed_twice, item.name, localNewName, upstreamItem.name);
        owner.showErrorDialog(R.string.dialog_rename_location, message, (d,w) -> observeEditing(upstreamItem, localNewName));
    }

}
