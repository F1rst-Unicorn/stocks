package de.njsm.stocks.frontend.setup;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import de.njsm.stocks.Config;
import de.njsm.stocks.R;

public class PrincipalsFragment extends AbstractStep {

    private EditText userName;

    private EditText userId;

    private EditText deviceName;

    private EditText deviceId;

    private EditText fingerprint;

    private EditText ticket;

    @Override
    public String name() {
        return "User";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_principals, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        userName = (EditText) getActivity().findViewById(R.id.user_name);
        userId = (EditText) getActivity().findViewById(R.id.user_id);
        deviceName = (EditText) getActivity().findViewById(R.id.device_name);
        deviceId = (EditText) getActivity().findViewById(R.id.device_id);
        fingerprint = (EditText) getActivity().findViewById(R.id.fingerprint);
        ticket = (EditText) getActivity().findViewById(R.id.ticket);
    }

    @Override
    public void onNext() {
        Bundle data = mStepper.getExtras();
        data.putString(Config.USERNAME_CONFIG, userName.getText().toString());
        data.putString(Config.DEVICE_NAME_CONFIG, deviceName.getText().toString());
        data.putInt(Config.UID_CONFIG, Integer.parseInt(userId.getText().toString()));
        data.putInt(Config.DID_CONFIG, Integer.parseInt(deviceId.getText().toString()));
        data.putString(Config.FPR_CONFIG, fingerprint.getText().toString());
        data.putString(Config.TICKET_CONFIG, ticket.getText().toString());
    }

    @Override
    public void onPrevious() {
        mStepper.getExtras().remove(Config.USERNAME_CONFIG);
    }

    @Override
    public void onStepVisible() {
        Bundle data = resolveDataSource();
        if (data == null) return;

        userName.setText(data.getString(Config.USERNAME_CONFIG));
        deviceName.setText(data.getString(Config.DEVICE_NAME_CONFIG));
        if (data.containsKey(Config.UID_CONFIG)) {
            userId.setText(String.valueOf(data.getInt(Config.UID_CONFIG)));
        }
        if (data.containsKey(Config.DID_CONFIG)) {
            deviceId.setText(String.valueOf(data.getInt(Config.DID_CONFIG)));
        }
        fingerprint.setText(data.getString(Config.FPR_CONFIG));
        ticket.setText(data.getString(Config.TICKET_CONFIG));

    }

    @Nullable
    private Bundle resolveDataSource() {
        Bundle data = getActivity().getIntent().getExtras();

        if (data == null) {
            Log.i(Config.LOG_TAG, "Using stepper extras");
            data = mStepper.getExtras();
            if (data == null) {
                Log.i(Config.LOG_TAG, "No data from previous steps available");
                return null;
            }
        } else {
            Log.i(Config.LOG_TAG, "Using intent extras");
        }
        return data;
    }
}
