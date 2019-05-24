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
