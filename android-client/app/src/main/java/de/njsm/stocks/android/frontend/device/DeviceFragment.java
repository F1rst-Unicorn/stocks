package de.njsm.stocks.android.frontend.device;


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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.user.UserFragment;
import de.njsm.stocks.android.frontend.util.NameValidator;
import de.njsm.stocks.android.frontend.util.SwipeCallback;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Logger;
import fj.data.Validation;

import javax.inject.Inject;

public class DeviceFragment extends BaseFragment {

    static final Logger LOG = new Logger(UserFragment.class);

    private DeviceFragmentArgs input;

    private ViewModelProvider.Factory viewModelFactory;

    UserDeviceViewModel viewModel;

    private SingleUserViewModel singleUserViewModel;

    private RecyclerView list;

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

        list = result.findViewById(R.id.user_detail_device_list);
        list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        result.findViewById(R.id.devices_fab).setOnClickListener(this::addDevice);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserDeviceViewModel.class);
        viewModel.init(input.getUserId());
        singleUserViewModel = ViewModelProviders.of(this, viewModelFactory).get(SingleUserViewModel.class);
        singleUserViewModel.init(input.getUserId());

        SwipeCallback<UserDevice> callback = new SwipeCallback<>(
                null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::initiateDeletion
        );
        viewModel.getDevices().observe(this, callback::setData);
        new ItemTouchHelper(callback).attachToRecyclerView(list);

        adapter = new DeviceAdapter(viewModel.getDevices(), this::onListItemClick);
        viewModel.getDevices().observe(this, d -> adapter.notifyDataSetChanged());
        list.setAdapter(adapter);

        initialiseSwipeRefresh(result, R.id.devices_swipe, viewModelFactory);

        singleUserViewModel.getUser().observe(this, u -> requireActivity().setTitle(u == null ? "" : u.name));
        return result;
    }

    private void initiateDeletion(UserDevice d) {
        Snackbar.make(list, R.string.dialog_device_was_deleted, Snackbar.LENGTH_SHORT)
                .setAction(R.string.action_undo, v -> {
                })
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
                                LiveData<StatusCode> result = viewModel.deleteUserDevice(d);
                                result.observe(DeviceFragment.this, DeviceFragment.this::maybeShowDeleteError);
                                break;
                        }
                    }
                })
                .show();
    }

    private void addDevice(View v) {
        EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
        textField.addTextChangedListener(
                new NameValidator(e -> textField.setError(getResources().getString(e))));
        textField.setHint(getResources().getString(R.string.hint_device_name));
        new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.dialog_new_device))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> {
                    dialog.dismiss();
                    String name = textField.getText().toString().trim();
                    LiveData<Validation<StatusCode, ServerTicket>> result = viewModel.addUserDevice(name, input.getUserId());
                    result.observe(this, data -> this.handleDeviceCreation(data, name));
                })
                .setNegativeButton(getResources().getString(android.R.string.cancel), (d, b) -> d.dismiss())
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

    private void onListItemClick(View view) {
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
