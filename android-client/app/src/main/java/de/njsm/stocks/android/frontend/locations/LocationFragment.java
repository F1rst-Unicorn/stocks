package de.njsm.stocks.android.frontend.locations;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class LocationFragment extends BaseFragment {

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
        View result = inflater.inflate(R.layout.template_swipe_list, container, false);

        result.findViewById(R.id.template_swipe_list_fab).setOnClickListener(this::addLocation);
        list = result.findViewById(R.id.template_swipe_list_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        addSwipeToDelete(list, viewModel.getLocations(), this::initiateDeletion);

        adapter = new LocationAdapter(viewModel.getLocations(), this::showContainedFood, this::edit);
        viewModel.getLocations().observe(this, u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, viewModelFactory);

        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private void addLocation(View view) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NonEmptyValidator(textField, this::showEmptyInputError));
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
                        result.observe(this, code -> this.treatEditCases(code, item, name));
                    })
                    .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                    .show();
        }
    }

    private void treatEditCases(StatusCode code, Location item, String name) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Location> newData = viewModel.getLocation(item.id);
            newData.observe(this, newLocation -> {
                if (newLocation != null && ! newLocation.equals(item)) {
                    compareLocations(item, name, newLocation);
                    newData.removeObservers(this);
                }
            });
        } else
            maybeShowEditError(code);
    }

    private void initiateDeletion(Location item) {
        showDeletionSnackbar(list, item,
                R.string.dialog_location_was_deleted,
                v -> adapter.notifyDataSetChanged(),
                this::performDeletion);
    }

    private void performDeletion(Location item) {
        adapter.notifyDataSetChanged();
        LiveData<StatusCode> result = viewModel.deleteLocation(item, false);
        result.observe(LocationFragment.this, code -> LocationFragment.this.treatDeletionCases(code, item));
    }

    void treatDeletionCases(StatusCode code, Location item) {
        if (code == StatusCode.INVALID_DATA_VERSION) {
            LiveData<Location> newData = viewModel.getLocation(item.id);
            newData.observe(this, newLocation -> {
                if (newLocation != null && ! newLocation.equals(item)) {
                    compareLocations(item, newLocation);
                    newData.removeObservers(this);
                }
            });
        } else if (code == StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION)
            offerCascadingDeletion(item);
        else
            maybeShowDeleteError(code);
    }

    private void compareLocations(Location item, String localNewName, Location upstreamItem) {
        String message = requireContext().getString(R.string.error_location_changed_twice, item.name, localNewName, upstreamItem.name);
        new AlertDialog.Builder(requireActivity())
                .setTitle(requireContext().getString(R.string.dialog_rename_location))
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    LiveData<StatusCode> result = viewModel.renameLocation(upstreamItem, localNewName);
                    result.observe(this, code -> this.treatEditCases(code, item, localNewName));
                    d.dismiss();
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }

    private void compareLocations(Location item, Location newLocation) {
        String message = requireContext().getString(R.string.error_location_changed, item.name, newLocation.name);
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.title_delete_location)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    LiveData<StatusCode> result = viewModel.deleteLocation(newLocation, false);
                    result.observe(this, this::maybeShowDeleteError);
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
                    result.observe(this, this::maybeShowDeleteError);
                    d.dismiss();
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
                .show();
    }
}
