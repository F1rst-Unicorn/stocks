package de.njsm.stocks.android.frontend;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.util.*;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

import java.util.List;

public class BaseFragment extends Fragment {

    private static final Logger LOG = new Logger(BaseFragment.class);

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null)
            LOG.d("starting with args " + getArguments());

    }

    protected void maybeShowAddError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getAddErrorMessage());
        }
    }

    public void maybeShowReadError(StatusCode code) {
        maybeShowReadError(requireActivity(), code);
    }

    public void maybeShowEditError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getEditErrorMessage());
        }
    }

    protected void maybeShowDeleteError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getDeleteErrorMessage());
        }
    }

    public static void maybeShowReadError(Activity a, StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(a, code.getReadErrorMessage());
        }
    }

    public static void showErrorMessage(Activity a, int resourceId) {
        new AlertDialog.Builder(a)
                .setTitle(R.string.title_error)
                .setMessage(resourceId)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(a.getResources().getString(R.string.dialog_ok), (d, w) -> d.dismiss())
                .create()
                .show();
    }

    protected void addFood(FoodViewModel viewModel) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NonEmptyValidator(textField, this::showEmptyInputError));
        textField.setHint(getResources().getString(R.string.hint_food));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_food))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    dialog.dismiss();
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addFood(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }

    protected void showEmptyInputError(EditText editText, Boolean isEmpty) {
        if (isEmpty)
            editText.setError(requireActivity().getString(R.string.error_may_not_be_empty));
        else
            editText.setError(null);
    }

    protected void initialiseSwipeRefresh(View view,
                                                      ViewModelProvider.Factory viewModelFactory) {
        initialiseSwipeRefresh(view, R.id.template_swipe_list_swipe, viewModelFactory);
    }

    protected RefreshViewModel initialiseSwipeRefresh(View view,
                                          int swiperId,
                                          ViewModelProvider.Factory viewModelFactory) {
        SwipeRefreshLayout refresher = view.findViewById(swiperId);
        RefreshViewModel refreshViewModel = ViewModelProviders.of(this, viewModelFactory).get(RefreshViewModel.class);
        refresher.setOnRefreshListener(new SwipeSyncCallback(this, refresher, refreshViewModel));
        return refreshViewModel;
    }

    protected <T> void showDeletionSnackbar(View view, T item,
                                            int messageId,
                                            Consumer<T> deletionCancler, Consumer<T> deleter) {
        Snackbar.make(view, messageId, Snackbar.LENGTH_SHORT)
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
                                deleter.accept(item);
                                break;
                        }
                    }
                })
                .show();
    }

    protected <T> void addSwipeToDelete(RecyclerView list, LiveData<List<T>> food, Consumer<T> deleter) {
        SwipeCallback<T> callback = new SwipeCallback<>(
                null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                deleter
        );
        food.observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(list);
    }
}
