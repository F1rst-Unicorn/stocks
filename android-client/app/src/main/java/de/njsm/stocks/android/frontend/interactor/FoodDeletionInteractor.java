package de.njsm.stocks.android.frontend.interactor;

import android.view.View;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.network.server.StatusCode;

public class FoodDeletionInteractor extends DeletionInteractor<Food> {

    private Function<Integer, LiveData<Food>> updater;

    public FoodDeletionInteractor(BaseFragment owner,
                                  View snackbarParent,
                                  Consumer<Food> deletionCancler,
                                  Function<Food, LiveData<StatusCode>> deleter,
                                  Function<Integer, LiveData<Food>> updater) {
        super(owner, snackbarParent, deletionCancler, deleter);
        this.updater = updater;
    }

    @Override
    protected int getSnackbarMessageId() {
        return R.string.dialog_food_was_deleted;
    }

    @Override
    protected void treatErrorCode(StatusCode code, Food item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Food> newData = updater.apply(item.id);
            newData.observe(owner, newItem -> {
                if (newItem != null && !newItem.equals(item)) {
                    compareFood(item, newItem);
                    newData.removeObservers(owner);
                }
            });
        } else
            owner.maybeShowDeleteError(code);
    }

    private void compareFood(Food item, Food upstreamItem) {
        String message = owner.requireContext().getString(R.string.error_food_changed, item.name, upstreamItem.name);
        owner.showErrorDialog(R.string.title_delete_food, message, (d, w) -> observeDeletion(upstreamItem));
    }
}
