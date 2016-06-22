package de.njsm.stocks.setup;


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

    protected String errorText;

    @Override
    public String name() {
        return "Server";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null) {
            return;
        }
        if (extras.containsKey(Config.serverNameConfig)) {
            ((EditText) getActivity().findViewById(R.id.server_url)).setText(
                    extras.getString(Config.serverNameConfig, "")
            );
        }
        if (extras.containsKey(Config.caPortConfig)) {
            ((EditText) getActivity().findViewById(R.id.ca_port)).setText(
                    String.valueOf(extras.getInt(Config.caPortConfig, 0))
            );
        }
        if (extras.containsKey(Config.sentryPortConfig)) {
            ((EditText) getActivity().findViewById(R.id.sentry_port)).setText(
                    String.valueOf(extras.getInt(Config.sentryPortConfig, 0))
            );
        }
        if (extras.containsKey(Config.serverPortConfig)) {
            ((EditText) getActivity().findViewById(R.id.server_port)).setText(
                    String.valueOf(extras.getInt(Config.serverPortConfig, 0))
            );
        }
    }

    @Override
    public boolean nextIf() {
        if (getServerName().equals("")) {
            errorText = getResources().getString(R.string.error_no_server_name);
            return false;
        }
        return true;
    }

    @Override
    public void onNext() {
        Bundle data = mStepper.getExtras();
        data.putString(Config.serverNameConfig, getServerName());
        data.putInt(Config.caPortConfig, getCaPort());
        data.putInt(Config.sentryPortConfig, getSentryPort());
        data.putInt(Config.serverPortConfig, getServerPort());
    }

    @Override
    public String error() {
        return errorText;
    }

    public String getServerName() {
        EditText textField = (EditText) getActivity().findViewById(R.id.server_url);
        return textField.getText().toString();
    }

    public boolean isExpertState() {
        Switch s = (Switch) getActivity().findViewById(R.id.expert_switch);
        return s.isChecked();
    }

    public int getCaPort() {
        int result;
        EditText textField = (EditText) getActivity().findViewById(R.id.ca_port);
        String text = textField.getText().toString();
        if (!text.equals("") && isExpertState()) {
            result = Integer.parseInt(textField.getText().toString());
        } else {
            result = 10910;
        }
        return result;
    }

    public int getSentryPort() {
        int result;
        EditText textField = (EditText) getActivity().findViewById(R.id.sentry_port);
        String text = textField.getText().toString();
        if (!text.equals("") && isExpertState()) {
            result = Integer.parseInt(textField.getText().toString());
        } else {
            result = 10911;
        }
        return result;
    }

    public int getServerPort() {
        int result;
        EditText textField = (EditText) getActivity().findViewById(R.id.server_port);
        String text = textField.getText().toString();
        if (!text.equals("") && isExpertState()) {
            result = Integer.parseInt(textField.getText().toString());
        } else {
            result = 10912;
        }
        return result;
    }
}
