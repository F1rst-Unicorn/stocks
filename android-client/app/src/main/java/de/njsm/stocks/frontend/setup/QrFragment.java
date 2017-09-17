package de.njsm.stocks.frontend.setup;

import android.os.Bundle;
import android.util.Log;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

public class QrFragment extends AbstractStep {

    @Override
    public String name() {
        return "QR Code step";
    }

    @Override
    public void onStepVisible() {
        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null && extras.containsKey(Config.USERNAME_CONFIG)) {
            Log.i(Config.LOG_TAG, "Already got data from extras, skipping QR step");
            ((SetupActivity) getActivity()).onNext();
        } else {
            Log.i(Config.LOG_TAG, "Starting QR code reader");
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    public void getQrResult(IntentResult res) {

        if (res.getContents() != null) {

            Log.d(Config.LOG_TAG, "Got QR data: " + res.getContents());
            String[] arguments = res.getContents().split("\n");
            Bundle data = mStepper.getExtras();

            if (arguments.length == 6) {
                data.putString(Config.USERNAME_CONFIG, arguments[0]);
                data.putString(Config.DEVICE_NAME_CONFIG, arguments[1]);
                data.putInt(Config.UID_CONFIG, Integer.parseInt(arguments[2]));
                data.putInt(Config.DID_CONFIG, Integer.parseInt(arguments[3]));
                data.putString(Config.FPR_CONFIG, arguments[4]);
                data.putString(Config.TICKET_CONFIG, arguments[5]);
            } else {
                Log.w(Config.LOG_TAG, "QR code data contained invalid data");
            }
        } else {
            Log.d(Config.LOG_TAG, "No data scanned");
        }
    }
}
