package de.njsm.stocks.android.frontend.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;

public class ServerFragment extends BaseFragment {

    private EditText serverUrl;

    private EditText caPort;

    private EditText sentryPort;

    private EditText serverPort;

    private Switch portDisplaySwitch;

    private Button next;

    private ServerFragmentArgs input;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_server, container, false);
        assert getArguments() != null;
        input = ServerFragmentArgs.fromBundle(getArguments());
        serverUrl = result.findViewById(R.id.fragment_server_url);
        caPort = result.findViewById(R.id.fragment_server_ca_port);
        sentryPort = result.findViewById(R.id.fragment_server_sentry_port);
        serverPort = result.findViewById(R.id.fragment_server_server_port);
        portDisplaySwitch = result.findViewById(R.id.fragment_server_expert_switch);
        next = result.findViewById(R.id.fragment_server_server_button);

        serverUrl.addTextChangedListener(new NonEmptyValidator(serverUrl, this::invalidateButton));
        portDisplaySwitch.setOnClickListener(this::onExpertSwitch);
        next.setOnClickListener(this::goToNextStep);

        requireActivity().setTitle(R.string.title_server);
        return result;
    }

    private void invalidateButton(EditText view, Boolean isEmpty) {
        next.setEnabled(!isEmpty);
        if (isEmpty) {
            String error = requireActivity().getResources().getString(R.string.error_may_not_be_empty);
            view.setError(error);
        } else {
            view.setError(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!input.getServerUrl().isEmpty()) {
            serverUrl.setText(input.getServerUrl());
            next.setEnabled(true);
        } else {
            if (serverUrl.getText().toString().isEmpty()) {
                next.setEnabled(false);
            }
        }
        if (input.getCaPort() != 0) {
            caPort.setText(String.valueOf(input.getCaPort()));
        }
        if (input.getSentryPort() != 0) {
            sentryPort.setText(String.valueOf(input.getSentryPort()));
        }
        if (input.getServerPort() != 0) {
            serverPort.setText(String.valueOf(input.getServerPort()));
        }
    }

    private void goToNextStep(View v) {
        ServerFragmentDirections.ActionNavFragmentServerToNavFragmentQr args =
                ServerFragmentDirections.actionNavFragmentServerToNavFragmentQr(
                getServerName(),
                getCaPort(),
                getSentryPort(),
                getServerPort(),
                input.getUsername())
                .setUserId(input.getUserId())
                .setDeviceName(input.getDeviceName())
                .setDeviceId(input.getDeviceId())
                .setFingerprint(input.getFingerprint())
                .setTicket(input.getTicket());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }

    private String getServerName() {
        return serverUrl.getText().toString();
    }

    private boolean isExpertState() {
        return portDisplaySwitch.isChecked();
    }

    private int getCaPort() {
        return getPort(caPort, 10910);
    }

    private int getSentryPort() {
        return getPort(sentryPort, 10911);
    }

    private int getServerPort() {
        return getPort(serverPort, 10912);
    }

    private int getPort(EditText guiSource, int defaultValue) {
        String text = guiSource.getText().toString();
        if (!text.isEmpty() && isExpertState()) {
            return Integer.parseInt(guiSource.getText().toString());
        } else {
            return defaultValue;
        }
    }

    private void onExpertSwitch(View view) {
        Switch s = (Switch) view;
        LinearLayout l = getView().findViewById(R.id.fragment_server_expert_options);
        if (l == null) {
            return;
        }
        if (s.isChecked()) {
            l.setVisibility(View.VISIBLE);
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setVisibility(View.VISIBLE);
            }
        } else {
            l.setVisibility(View.GONE);
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }
}
