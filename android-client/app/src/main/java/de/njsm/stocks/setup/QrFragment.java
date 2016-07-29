package de.njsm.stocks.setup;

import android.os.Bundle;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import de.njsm.stocks.Config;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

public class QrFragment extends AbstractStep {


    @Override
    public String name() {
        return "QR Code step";
    }


    @Override
    public void onStepVisible() {
        // start qr reader
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey(Config.usernameConfig)) {
            ((SetupActivity) getActivity()).onNext();
        } else {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    public void getQrResult(IntentResult res) {

        if (res.getContents() != null) {

            String[] arguments = res.getContents().split("\n");
            Bundle data = mStepper.getExtras();

            if (arguments.length == 6) {
                data.putString(Config.usernameConfig, arguments[0]);
                data.putString(Config.deviceNameConfig, arguments[1]);
                data.putInt(Config.uidConfig, Integer.parseInt(arguments[2]));
                data.putInt(Config.didConfig, Integer.parseInt(arguments[3]));
                data.putString(Config.fprConfig, arguments[4]);
                data.putString(Config.ticketConfig, arguments[5]);
            }
        }
    }


}
