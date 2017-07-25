package de.njsm.stocks.frontend.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import de.njsm.stocks.Config;
import de.njsm.stocks.R;

public class ServerFragment extends AbstractStep {

    private String errorText;

    private EditText serverUrl;

    private EditText caPort;

    private EditText sentryPort;

    private EditText serverPort;

    private Switch portDisplaySwitch;

    @Override
    public String name() {
        return "Server";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        serverUrl = (EditText) getActivity().findViewById(R.id.server_url);
        caPort = (EditText) getActivity().findViewById(R.id.ca_port);
        sentryPort = (EditText) getActivity().findViewById(R.id.sentry_port);
        serverPort = (EditText) getActivity().findViewById(R.id.server_port);
        portDisplaySwitch = (Switch) getActivity().findViewById(R.id.expert_switch);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null) {
            return;
        }
        serverUrl.setText(extras.getString(Config.SERVER_NAME_CONFIG, ""));

        if (extras.containsKey(Config.CA_PORT_CONFIG)) {
            caPort.setText(String.valueOf(extras.getInt(Config.CA_PORT_CONFIG, 0)));
        }
        if (extras.containsKey(Config.SENTRY_PORT_CONFIG)) {
            sentryPort.setText(String.valueOf(extras.getInt(Config.SENTRY_PORT_CONFIG, 0)));
        }
        if (extras.containsKey(Config.SERVER_PORT_CONFIG)) {
            serverPort.setText(String.valueOf(extras.getInt(Config.SERVER_PORT_CONFIG, 0)));
        }
    }

    @Override
    public boolean nextIf() {
        if (getServerName().isEmpty()) {
            errorText = getResources().getString(R.string.error_no_server_name);
            return false;
        }
        return true;
    }

    @Override
    public void onNext() {
        Bundle data = mStepper.getExtras();
        data.putString(Config.SERVER_NAME_CONFIG, getServerName());
        data.putInt(Config.CA_PORT_CONFIG, getCaPort());
        data.putInt(Config.SENTRY_PORT_CONFIG, getSentryPort());
        data.putInt(Config.SERVER_PORT_CONFIG, getServerPort());
    }

    @Override
    public String error() {
        return errorText;
    }

    public String getServerName() {
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
}
