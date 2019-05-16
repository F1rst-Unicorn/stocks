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

        adapter = new LocationAdapter(viewModel.getLocations(),
                this::showContainedFood,
                v -> this.editInternally(v, viewModel.getLocations(), R.string.dialog_rename_location, this::observeRenaming));
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
                    String name = textField.getText().toString().trim();
                    LiveData<StatusCode> result = viewModel.addLocation(name);
                    result.observe(this, this::maybeShowAddError);
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), this::doNothing)
                .show();
    }

    private void showContainedFood(View view) {

    }

    private void initiateDeletion(Location item) {
        showDeletionSnackbar(list, item,
                R.string.dialog_location_was_deleted,
                v -> adapter.notifyDataSetChanged(),
                this::observeDeletion);
    }

    private void observeRenaming(Location item, String name) {
        LiveData<StatusCode> result = viewModel.renameLocation(item, name);
        result.observe(this, code -> this.treatRenamingCases(code, item, name));
    }

    private void observeDeletion(Location item) {
        adapter.notifyDataSetChanged();
        LiveData<StatusCode> result = viewModel.deleteLocation(item, false);
        result.observe(LocationFragment.this, code -> LocationFragment.this.treatDeletionCases(code, item));
    }

    private void treatRenamingCases(StatusCode code, Location item, String name) {
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
        showErrorDialog(R.string.dialog_rename_location, message,
                (d, w) -> this.observeRenaming(upstreamItem, localNewName));
    }

    private void compareLocations(Location item, Location newLocation) {
        String message = requireContext().getString(R.string.error_location_changed, item.name, newLocation.name);
        showErrorDialog(R.string.title_delete_location, message, (d, w) -> observeDeletion(newLocation));
    }

    private void offerCascadingDeletion(Location item) {
        String message = requireContext().getString(R.string.error_location_foreign_key_violation, item.name);
        showErrorDialog(R.string.title_delete_location, message, (d, w) -> {
            LiveData<StatusCode> result = viewModel.deleteLocation(item, true);
            result.observe(this, this::maybeShowDeleteError);
        });
    }
}
