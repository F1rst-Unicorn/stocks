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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.interactor.LocationDeletionInteractor;
import de.njsm.stocks.android.frontend.interactor.LocationEditInteractor;
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

        LocationEditInteractor editor = new LocationEditInteractor(
                this,
                viewModel::renameLocation,
                viewModel::getLocation
        );

        adapter = new LocationAdapter(viewModel.getLocations(),
                this::showContainedFood,
                v -> this.editInternally(v,
                        viewModel.getLocations(),
                        R.string.dialog_rename_location,
                        editor::observeEditing));
        viewModel.getLocations().observe(this, u -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        LocationDeletionInteractor interactor = new LocationDeletionInteractor(
                this, result,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteLocation(i, false),
                i -> viewModel.getLocation(i),
                i -> viewModel.deleteLocation(i, true));
        addSwipeToDelete(list, viewModel.getLocations(), interactor::initiateDeletion);

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
        LocationAdapter.ViewHolder holder = (LocationAdapter.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<Location> data = viewModel.getLocations().getValue();
        if (data != null) {
            int id = data.get(position).id;
            LocationFragmentDirections.ActionNavFragmentLocationsToNavFragmentFood args =
                    LocationFragmentDirections.actionNavFragmentLocationsToNavFragmentFood()
                    .setLocation(id);
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                    .navigate(args);
        }
    }
}
