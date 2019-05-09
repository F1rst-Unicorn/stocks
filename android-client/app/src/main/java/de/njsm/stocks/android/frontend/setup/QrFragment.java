package de.njsm.stocks.android.frontend.setup;


import android.os.Bundle;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;

public class QrFragment extends AbstractStep {

    private static final Logger LOG = new Logger(QrFragment.class);

    @Override
    public String name() {
        return "QR Code step";
    }

    @Override
    public void onStepVisible() {
        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null && extras.containsKey(Config.USERNAME_CONFIG)) {
            LOG.i("Already got data from extras, skipping QR step");
            ((SetupActivity) getActivity()).onNext();
        } else {
            LOG.i("Starting QR code reader");
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    void getQrResult(IntentResult res) {

        if (res.getContents() != null) {

            LOG.d("Got QR data: " + res.getContents());
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
                LOG.w("QR code data contained invalid data");
            }
        } else {
            LOG.d("No data scanned");
        }
    }
}
