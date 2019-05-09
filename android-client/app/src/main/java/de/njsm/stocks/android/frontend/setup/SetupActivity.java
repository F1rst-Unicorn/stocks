package de.njsm.stocks.android.frontend.setup;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import com.github.fcannizzaro.materialstepper.style.DotStepper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.startup.StartupActivity;
import de.njsm.stocks.android.util.Logger;

public class SetupActivity extends DotStepper {

    private static final Logger LOG = new Logger(SetupActivity.class);

    public static final String SETUP_FINISHED = "de.njsm.stocks.android.frontend.setup.SetupActivity.SETUP_FINISHED";

    protected QrFragment qrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getResources().getString(R.string.title_connection_setup));

        qrFragment = new QrFragment();

        addStep(new ServerFragment());
        addStep(qrFragment);
        addStep(new PrincipalsFragment());

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onComplete(Bundle data) {
        LOG.i("onComplete()");
        Intent i = new Intent(this, StartupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(SETUP_FINISHED, SETUP_FINISHED);
        i.putExtras(getExtras());
        startActivity(i);
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            LOG.d("Got QR code from intent");
            qrFragment.getQrResult(scanResult);
            onNext();
        } else {
            LOG.d("Got invalid result from activity");
        }
    }

    public void onExpertPush(View view) {
        Switch s = (Switch) view;
        LinearLayout l = findViewById(R.id.expert_options);
        if (l == null) {
            return;
        }
        if (s.isChecked()) {
            l.setVisibility(View.VISIBLE);
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setVisibility(View.VISIBLE);
            }
        } else {
            l.setVisibility(View.GONE);
            for (int i = 0; i < l.getChildCount(); i++) {
                l.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

}
