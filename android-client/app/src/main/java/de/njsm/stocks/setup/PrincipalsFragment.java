package de.njsm.stocks.setup;


import android.os.Bundle;
import android.util.Log;
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
    public void onNext() {
        Bundle data = mStepper.getExtras();
        data.putString(Config.usernameConfig, ((EditText) getActivity().findViewById(R.id.user_name)).getText().toString());
        data.putString(Config.deviceNameConfig, ((EditText) getActivity().findViewById(R.id.device_name)).getText().toString());
        data.putInt(Config.uidConfig, Integer.parseInt(((EditText) getActivity().findViewById(R.id.user_id)).getText().toString()));
        data.putInt(Config.didConfig, Integer.parseInt(((EditText) getActivity().findViewById(R.id.device_id)).getText().toString()));
        data.putString(Config.fprConfig, ((EditText) getActivity().findViewById(R.id.fingerprint)).getText().toString());
        data.putString(Config.ticketConfig, ((EditText) getActivity().findViewById(R.id.ticket)).getText().toString());
    }

    @Override
    public void onPrevious() {
        mStepper.getExtras().remove(Config.usernameConfig);
    }

    @Override
    public void onStepVisible() {
        Bundle data = getActivity().getIntent().getExtras();
        if (data == null) {
            Log.i(Config.log, "Using stepper extras");
            data = mStepper.getExtras();
        }
        if (data == null) {
            return;
        }
        ((EditText) getActivity().findViewById(R.id.user_name)).setText(data.getString(Config.usernameConfig));
        ((EditText) getActivity().findViewById(R.id.device_name)).setText(data.getString(Config.deviceNameConfig));
        ((EditText) getActivity().findViewById(R.id.user_id)).setText(String.valueOf(data.getInt(Config.uidConfig)));
        ((EditText) getActivity().findViewById(R.id.device_id)).setText(String.valueOf(data.getInt(Config.didConfig)));
        ((EditText) getActivity().findViewById(R.id.fingerprint)).setText(data.getString(Config.fprConfig));
        ((EditText) getActivity().findViewById(R.id.ticket)).setText(data.getString(Config.ticketConfig));

    }
}
