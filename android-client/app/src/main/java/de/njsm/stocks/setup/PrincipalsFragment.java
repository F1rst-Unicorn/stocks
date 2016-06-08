package de.njsm.stocks.setup;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import de.njsm.stocks.Config;
import de.njsm.stocks.R;

public class PrincipalsFragment extends AbstractStep {

    public PrincipalsFragment() {
    }

    @Override
    public String name() {
        return "User";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_principals, container, false);
    }

    @Override
    public void onPrevious() {
        mStepper.getExtras().remove(Config.username);
    }

    @Override
    public void onStepVisible() {
        Bundle data = mStepper.getExtras();
        ((EditText) getActivity().findViewById(R.id.user_name)).setText(data.getString(Config.username));
        ((EditText) getActivity().findViewById(R.id.device_name)).setText(data.getString(Config.deviceName));
        ((EditText) getActivity().findViewById(R.id.user_id)).setText(String.valueOf(data.getInt(Config.uid)));
        ((EditText) getActivity().findViewById(R.id.device_id)).setText(String.valueOf(data.getInt(Config.did)));
        ((EditText) getActivity().findViewById(R.id.fingerprint)).setText(data.getString(Config.fpr));
        ((EditText) getActivity().findViewById(R.id.ticket)).setText(data.getString(Config.ticket));

    }
}
