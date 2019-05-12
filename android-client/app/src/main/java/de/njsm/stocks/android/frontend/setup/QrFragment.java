package de.njsm.stocks.android.frontend.setup;


import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import com.google.zxing.integration.android.IntentIntegrator;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.util.Logger;

public class QrFragment extends BaseFragment {

    private static final Logger LOG = new Logger(QrFragment.class);

    private QrFragmentArgs input;

    private SetupBroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
        assert getArguments() != null;
        input = QrFragmentArgs.fromBundle(getArguments());

        receiver = new SetupBroadcastReceiver(this);
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
            LOG.i("Starting QR code reader");
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
        requireActivity().setTitle(R.string.title_qr_code_scan);
        getArguments().putString("username", null);
        return view;
    }

    void getQrResult(@Nullable String content) {
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

        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }
}
