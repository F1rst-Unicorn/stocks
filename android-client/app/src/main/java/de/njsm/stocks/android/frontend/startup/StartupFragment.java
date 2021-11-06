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

package de.njsm.stocks.android.frontend.startup;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.service.SetupHandler;
import de.njsm.stocks.android.service.SetupService;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;

public class StartupFragment extends BaseFragment {

    private static final Logger LOG = new Logger(StartupFragment.class);

    public static final String ACTION_INPUT_AVAILABLE = "de.njsm.stocks.backend.setup.SetupService.ACTION_INPUT_AVAILABLE";

    private ProgressDialog dialog;

    private StartupBroadcastReceiver receiver;

    private StartupFragmentArgs input;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments() == null ? new Bundle() : getArguments();
        input = StartupFragmentArgs.fromBundle(args);

        LinearLayout view = new LinearLayout(getActivity());
        SharedPreferences prefs = requireActivity().getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);
        requireActivity().setTitle(R.string.app_name);

        if (input.getUsername() != null) {
            LOG.i("Setup data is available, sending to setup service");
            startProgressDialog();
            setupBroadcastReceiver();
            sendInputToBackgroundService();
        } else if (! prefs.contains(Config.USERNAME_CONFIG)) {
            LOG.i("First start up, redirecting to setup fragments");
            startBackgroundService();
            startSetupByUser();
        } else {
            LOG.i("Subsequent startup, proceeding normally");
            goToMainActivity();
        }
        return view;
    }

    void setDialogMessage(int id) {
        if (dialog != null) {
            dialog.setMessage(getResources().getString(id));
        }
    }

    void showTerminationDialog(int title, int message, boolean isSuccess) {
        Activity a = getActivity();
        assert a != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        if (isSuccess) {
            builder.setIcon(R.drawable.ic_check_black_24dp)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, which) -> {
                        getActivity().stopService(new Intent(getActivity(), SetupService.class));
                        goToMainActivity();
                    });
        } else {
            builder.setPositiveButton(getResources().getString(R.string.dialog_retry), (dialog, which) -> {
                startSetupByUser();
            }).setNegativeButton(getResources().getString(R.string.dialog_abort),(dialog, which) -> {
                getActivity().stopService(new Intent(a, SetupService.class));
                getActivity().finish();
            }).setIcon(R.drawable.ic_error_black_24dp);
        }

        AlertDialog messageDialog = builder.create();
        dialog.dismiss();
        ((MainActivity) requireActivity()).getResource().decrement();
        messageDialog.show();
        LocalBroadcastManager.getInstance(a).unregisterReceiver(receiver);
    }

    private void startBackgroundService() {
        assert getActivity() != null;
        Intent i = new Intent(getActivity(), SetupService.class);
        getActivity().startService(i);
    }

    private void sendInputToBackgroundService() {
        assert getActivity() != null;
        LOG.d("publishing setup data");
        Intent i = new Intent();
        i.setAction(ACTION_INPUT_AVAILABLE);
        i.putExtra(Config.SERVER_NAME_CONFIG, input.getServerUrl());
        i.putExtra(Config.CA_PORT_CONFIG, input.getCaPort());
        i.putExtra(Config.SENTRY_PORT_CONFIG, input.getSentryPort());
        i.putExtra(Config.SERVER_PORT_CONFIG, input.getServerPort());
        i.putExtra(Config.USERNAME_CONFIG, input.getUsername());
        i.putExtra(Config.UID_CONFIG, input.getUserId());
        i.putExtra(Config.DEVICE_NAME_CONFIG, input.getDeviceName());
        i.putExtra(Config.DID_CONFIG, input.getDeviceId());
        i.putExtra(Config.FPR_CONFIG, input.getFingerprint());
        i.putExtra(Config.TICKET_CONFIG, input.getTicket());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
    }

    private void setupBroadcastReceiver() {
        assert getActivity() != null;
        LOG.d("registering setup brr");
        receiver = new StartupBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SetupHandler.ACTION_DONE);
        filter.addAction(SetupHandler.ACTION_UPDATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
    }

    private void startProgressDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getResources().getString(R.string.dialog_registering));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        ((MainActivity) requireActivity()).getResource().increment();
    }

    private void startSetupByUser() {
        StartupFragmentDirections.ActionNavFragmentStartupToNavFragmentQr args =
                StartupFragmentDirections.actionNavFragmentStartupToNavFragmentQr(null)
                .setServerUrl(input.getServerUrl())
                .setCaPort(input.getCaPort())
                .setSentryPort(input.getSentryPort())
                .setServerPort(input.getServerPort())
                .setUsername(input.getUsername())
                .setUserId(input.getUserId())
                .setDeviceName(input.getDeviceName())
                .setDeviceId(input.getDeviceId())
                .setFingerprint(input.getFingerprint())
                .setTicket(input.getTicket());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }

    private void goToMainActivity() {
        ((DrawerLayout) requireActivity().findViewById(R.id.main_drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(R.id.action_nav_fragment_startup_to_nav_fragment_outline);
    }
}
