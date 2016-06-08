package de.njsm.stocks.setup;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        if (mStepper.getExtras().containsKey(Config.serverName)) {
            ((EditText) getActivity().findViewById(R.id.server_url)).setText(
                    mStepper.getExtras().getString(Config.serverName, "")
            );
        }
        if (mStepper.getExtras().containsKey(Config.caPort)) {
            ((EditText) getActivity().findViewById(R.id.ca_port)).setText(
                    String.valueOf(mStepper.getExtras().getInt(Config.caPort, 0))
            );
        }
        if (mStepper.getExtras().containsKey(Config.sentryPort)) {
            ((EditText) getActivity().findViewById(R.id.sentry_port)).setText(
                    String.valueOf(mStepper.getExtras().getInt(Config.sentryPort, 0))
            );
        }
        if (mStepper.getExtras().containsKey(Config.serverPort)) {
            ((EditText) getActivity().findViewById(R.id.server_port)).setText(
                    String.valueOf(mStepper.getExtras().getInt(Config.serverPort, 0))
            );
        }
    }

    @Override
    public boolean nextIf() {
        if (getServerName().equals("")) {
            errorText = "No server name set";
            return false;
        }
        return true;
    }

    @Override
    public void onNext() {
        Bundle data = mStepper.getExtras();
        data.putString(Config.serverName, getServerName());
        data.putInt(Config.caPort, getCaPort());
        data.putInt(Config.sentryPort, getSentryPort());
        data.putInt(Config.serverPort, getServerPort());
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
