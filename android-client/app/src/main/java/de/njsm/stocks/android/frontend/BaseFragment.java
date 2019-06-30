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

package de.njsm.stocks.android.frontend;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Positionable;
import de.njsm.stocks.android.frontend.emptyfood.FoodViewModel;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.frontend.util.SwipeCallback;
import de.njsm.stocks.android.frontend.util.SwipeSyncCallback;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

import java.util.List;
import java.util.function.BiConsumer;

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

    public void maybeShowDeleteError(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(requireActivity(), code.getDeleteErrorMessage());
        }
    }

    public static void maybeShowReadError(Activity a, StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            showErrorMessage(a, code.getReadErrorMessage());
        }
    }

    private static void showErrorMessage(Activity a, int resourceId) {
        new AlertDialog.Builder(a)
                .setTitle(R.string.title_error)
                .setMessage(resourceId)
                .setCancelable(true)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(a.getResources().getString(R.string.dialog_ok), (d, w) -> {})
                .create()
                .show();
    }

    public void showErrorDialog(int titleId, String message, DialogInterface.OnClickListener doer) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(requireContext().getString(titleId))
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, doer)
                .setNegativeButton(getString(android.R.string.cancel), this::doNothing)
                .show();
    }

    protected void addFood(FoodViewModel viewModel) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NonEmptyValidator(textField, this::showEmptyInputError));
        textField.setHint(getString(R.string.hint_food));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.dialog_new_food))
                .setView(textField)
                .setPositiveButton(getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addFood(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getString(android.R.string.cancel), (d, b) -> {})
                .show();
    }

    protected void showEmptyInputError(EditText editText, Boolean isEmpty) {
        if (isEmpty)
            editText.setError(getString(R.string.error_may_not_be_empty));
        else
            editText.setError(null);
    }

    protected void initialiseSwipeRefresh(View view, ViewModelProvider.Factory viewModelFactory) {
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

    protected <T> void editInternally(View view,
                                      LiveData<List<T>> data,
                                      int messageId,
                                      BiConsumer<T, String> editer) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<T> list = data.getValue();
        if (list != null) {
            T item = list.get(position);
            EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getString(R.string.hint_new_name));
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(messageId))
                    .setView(textField)
                    .setPositiveButton(getString(R.string.dialog_ok), (dialog, whichButton) -> {
                        String name = textField.getText().toString().trim();
                        editer.accept(item, name);
                    })
                    .setNegativeButton(getString(android.R.string.cancel), this::doNothing)
                    .show();
        }
    }

    protected <T extends Positionable> void addSwipeToDelete(RecyclerView list, LiveData<List<T>> data, Consumer<T> deleter) {
        addSwipeToDelete(list, data, R.drawable.ic_delete_white_24dp, deleter);
    }

    protected <T extends Positionable> void addSwipeToDelete(RecyclerView list, LiveData<List<T>> data, int icon, Consumer<T> deleter) {
        SwipeCallback<T> callback = new SwipeCallback<>(
                null,
                ContextCompat.getDrawable(requireActivity(), icon),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                deleter
        );
        data.observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(list);
    }

    protected <T extends Positionable> void addBidirectionalSwiper(RecyclerView list, LiveData<List<T>> data, int icon, Consumer<T> deleter, Consumer<T> leftSwiper) {
        SwipeCallback<T> callback = new SwipeCallback<>(
                null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                ContextCompat.getDrawable(requireActivity(), icon),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                deleter,
                leftSwiper
        );
        data.observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(list);
    }

    protected boolean probeForCameraPermission() {
        return probeForCameraPermission(true);
    }

    protected boolean probeForCameraPermission(boolean showRationale) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (showRationale && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CAMERA)) {
                String message = getString(R.string.text_camera_explanation);
                showErrorDialog(R.string.title_camera_permission,
                        message,
                        (d,w) -> this.probeForCameraPermission(false));
                return false;
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }
            return false;
        } else {
            return true;
        }
    }

    protected void doNothing(DialogInterface dialogInterface, int i) {}

    protected void doNothing(View dialogInterface) {}
}
