package de.njsm.stocks.android.frontend.locations;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.SwipeCallback;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class LocationFragment extends BaseFragment {

    static final Logger LOG = new Logger(LocationFragment.class);

    private ViewModelProvider.Factory viewModelFactory;

    LocationViewModel viewModel;

    private RecyclerView list;

    RecyclerView.Adapter<LocationAdapter.ViewHolder> adapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_locations, container, false);

        result.findViewById(R.id.locations_fab).setOnClickListener(this::addLocation);
        list = result.findViewById(R.id.locations_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        SwipeCallback<Location> callback = new SwipeCallback<>(
                null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::initiateDeletion
        );
        viewModel.getLocations().observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(list);

        adapter = new LocationAdapter(viewModel.getLocations(), this::showContainedFood, this::edit);
        viewModel.getLocations().observe(this, u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, R.id.locations_swipe, viewModelFactory);

        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void addLocation(View view) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.setHint(getResources().getString(R.string.hint_location));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_location))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    dialog.dismiss();
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addLocation(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }

    private void showContainedFood(View view) {

    }

    private void edit(View view) {
        LocationAdapter.ViewHolder holder = (LocationAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<Location> list = viewModel.getLocations().getValue();
        if (list != null) {
            Location item = list.get(position);
            EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_new_name));
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getResources().getString(R.string.dialog_rename_location))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                        dialog.dismiss();
                        String name = textField.getText().toString().trim();
                        LiveData<StatusCode> result = viewModel.renameLocation(item, name);
                        result.observe(this, this::maybeShowEditError);
                    })
                    .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                    .show();
        }
    }

    private void initiateDeletion(Location item) {
        Snackbar.make(list, R.string.dialog_location_was_deleted, Snackbar.LENGTH_SHORT)
                .setAction(R.string.action_undo, v -> {})
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        switch (event) {
                            case DISMISS_EVENT_ACTION:
                                adapter.notifyDataSetChanged();
                                LOG.d("Deletion cancelled");
                                break;
                            case DISMISS_EVENT_CONSECUTIVE:
                            case DISMISS_EVENT_MANUAL:
                            case DISMISS_EVENT_SWIPE:
                            case DISMISS_EVENT_TIMEOUT:
                                adapter.notifyDataSetChanged();
                                LiveData<StatusCode> result = viewModel.deleteLocation(item, false);
                                result.observe(LocationFragment.this, code -> LocationFragment.this.treatDeletionCases(code, item));
                                break;
                        }
                    }
                })
                .show();
    }

    private void treatDeletionCases(StatusCode code, Location item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Location> newData = viewModel.getLocation(item.id);
            newData.observe(this, newLocation -> {
                if (newLocation != null) {
                    compareLocations(item, newLocation);
                }
                newData.removeObservers(this);
            });
        } else if (code == StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION)
            offerCascadingDeletion(item);
        else
            maybeShowDeleteError(code);
    }

    private void compareLocations(Location item, Location newLocation) {
        String message = requireContext().getString(R.string.error_location_changed, item.name, newLocation.name);
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.title_delete_location)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    LiveData<StatusCode> result = viewModel.deleteLocation(newLocation, false);
                    result.observe(LocationFragment.this, LocationFragment.this::maybeShowDeleteError);
                    d.dismiss();
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }

    private void offerCascadingDeletion(Location item) {
        String message = requireContext().getString(R.string.error_location_foreign_key_violation, item.name);
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.title_delete_location)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    LiveData<StatusCode> result = viewModel.deleteLocation(item, true);
                    result.observe(LocationFragment.this, LocationFragment.this::maybeShowDeleteError);
                    d.dismiss();
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }
}
