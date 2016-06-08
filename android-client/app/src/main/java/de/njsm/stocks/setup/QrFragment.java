package de.njsm.stocks.setup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import java.util.List;

import de.njsm.stocks.Config;
import de.njsm.stocks.R;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

public class QrFragment extends AbstractStep {


    @Override
    public String name() {
        return "QR Code step";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStepVisible() {
        // start qr reader
        if (mStepper.getExtras().containsKey(Config.username)) {
            ((SetupActivity) getActivity()).onNext();
        } else {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    public void getQrResult(IntentResult res) {

        String[] arguments = res.getContents().split("\n");
        Bundle data = mStepper.getExtras();

        if (arguments.length == 6) {
            data.putString(Config.username, arguments[0]);
            data.putString(Config.deviceName, arguments[1]);
            data.putInt(Config.uid, Integer.parseInt(arguments[2]));
            data.putInt(Config.did, Integer.parseInt(arguments[3]));
            data.putString(Config.fpr, arguments[4]);
            data.putString(Config.ticket, arguments[5]);
        }

    }


}
