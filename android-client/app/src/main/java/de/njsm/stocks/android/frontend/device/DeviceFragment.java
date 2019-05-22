package de.njsm.stocks.android.frontend.device;


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
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.interactor.DeviceDeletionInteractor;
import de.njsm.stocks.android.frontend.util.NameValidator;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.android.network.server.StatusCode;
import fj.data.Validation;

import javax.inject.Inject;

public class DeviceFragment extends BaseFragment {

    private DeviceFragmentArgs input;

    private ViewModelProvider.Factory viewModelFactory;

    UserDeviceViewModel viewModel;

    private SingleUserViewModel singleUserViewModel;

    DeviceAdapter adapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_devices, container, false);
        assert getArguments() != null;
        input = DeviceFragmentArgs.fromBundle(getArguments());

        RecyclerView list = result.findViewById(R.id.fragment_devices_device_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        result.findViewById(R.id.fragment_devices_fab).setOnClickListener(this::addDevice);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserDeviceViewModel.class);
        viewModel.init(input.getUserId());
        singleUserViewModel = ViewModelProviders.of(this, viewModelFactory).get(SingleUserViewModel.class);
        singleUserViewModel.init(input.getUserId());

        adapter = new DeviceAdapter(viewModel.getDevices(), this::doNothing);
        viewModel.getDevices().observe(this, d -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        DeviceDeletionInteractor interactor = new DeviceDeletionInteractor(
                this, result,
                i -> adapter.notifyDataSetChanged(),
                i -> viewModel.deleteUserDevice(i));
        addSwipeToDelete(list, viewModel.getDevices(), interactor::initiateDeletion);

        initialiseSwipeRefresh(result, R.id.fragment_devices_swipe, viewModelFactory);

        singleUserViewModel.getUser().observe(this, u -> requireActivity().setTitle(u == null ? "" : u.name));
        return result;
    }

    private void addDevice(View v) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NameValidator(e -> textField.setError(getResources().getString(e))));
        textField.addTextChangedListener(
                new NonEmptyValidator(textField, this::showEmptyInputError));
        textField.setHint(getResources().getString(R.string.hint_device_name));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_device))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    String name = textField.getText().toString().trim();
                    LiveData<Validation<StatusCode, ServerTicket>> result = viewModel.addUserDevice(name, input.getUserId());
                    result.observe(this, data -> {
                        result.removeObservers(this);
                        this.handleDeviceCreation(data, name);
                    });
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), this::doNothing)
                .show();
    }

    private void handleDeviceCreation(Validation<StatusCode, ServerTicket> data, String name) {
        if (data.isFail()) {
            maybeShowAddError(data.fail());
        } else {
            ServerTicket ticket = data.success();
            User owner = singleUserViewModel.getUser().getValue();
            if (owner == null)
                return;
            DeviceFragmentDirections.ActionNavFragmentDevicesToNavFragmentQrCode args =
                    DeviceFragmentDirections.actionNavFragmentDevicesToNavFragmentQrCode(
                            owner.id,
                            owner.name,
                            ticket.deviceId,
                            name,
                            ticket.ticket
                    );
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
        }
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
