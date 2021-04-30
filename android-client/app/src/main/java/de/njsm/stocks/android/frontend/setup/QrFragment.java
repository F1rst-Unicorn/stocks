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

package de.njsm.stocks.android.frontend.setup;


import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import com.google.zxing.integration.android.IntentIntegrator;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;

public class QrFragment extends BaseFragment {

    private static final Logger LOG = new Logger(QrFragment.class);

    private QrFragmentArgs input;

    private SetupBroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_qr_setup, container, false);
        assert getArguments() != null;
        input = QrFragmentArgs.fromBundle(getArguments());

        IdlingResource resource = ((MainActivity) getActivity()).getResource();

        receiver = new SetupBroadcastReceiver(this, resource);
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_QR_CODE_SCANNED);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);

        if (input.getUsername() != null) {
            LOG.i("Already got data from extras, skipping QR step");
            QrFragmentDirections.ActionNavFragmentQrToNavFragmentPrincipals args =
                    QrFragmentDirections.actionNavFragmentQrToNavFragmentPrincipals(
                    input.getServerUrl(),
                    input.getCaPort(),
                    input.getSentryPort(),
                    input.getServerPort(),
                    input.getUsername())
                    .setUserId(input.getUserId())
                    .setDeviceName(input.getDeviceName())
                    .setDeviceId(input.getDeviceId())
                    .setFingerprint(input.getFingerprint())
                    .setTicket(input.getTicket());
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
        } else {
            if (probeForCameraPermission()) {
                LOG.i("Starting QR code reader");
                resource.increment();
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
            }
        }
        requireActivity().setTitle(R.string.title_qr_code_scan);
        getArguments().putString("username", null);
        result.findViewById(R.id.fragment_qr_setup_button).setOnClickListener(this::goOnWithoutQrCode);
        return result;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void goOnWithoutQrCode(View view) {
        QrFragmentDirections.ActionNavFragmentQrToNavFragmentPrincipals args =
                QrFragmentDirections.actionNavFragmentQrToNavFragmentPrincipals(
                        input.getServerUrl(),
                        input.getCaPort(),
                        input.getSentryPort(),
                        input.getServerPort(),
                        null);
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }

    void getQrResult(@Nullable String content) {
        if (getActivity() == null) {
            return;
        }

        QrFragmentDirections.ActionNavFragmentQrToNavFragmentPrincipals args =
                QrFragmentDirections.actionNavFragmentQrToNavFragmentPrincipals(
                input.getServerUrl(),
                input.getCaPort(),
                input.getSentryPort(),
                input.getServerPort(),
                null);

        if (content != null) {
            String[] arguments = content.split("\n");
            if (arguments.length == 6) {
                args.setUsername(arguments[0])
                        .setDeviceName(arguments[1])
                        .setUserId(Integer.parseInt(arguments[2]))
                        .setDeviceId(Integer.parseInt(arguments[3]))
                        .setFingerprint(arguments[4])
                        .setTicket(arguments[5]);
            } else {
                LOG.w("QR code doesn't contain stocks registration info");
            }
        } else {
            LOG.i("QR code was skipped");
        }

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }
}
