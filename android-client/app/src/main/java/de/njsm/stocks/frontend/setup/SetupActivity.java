package de.njsm.stocks.frontend.setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import com.github.fcannizzaro.materialstepper.style.DotStepper;
import de.njsm.stocks.Config;
import de.njsm.stocks.MainActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

public class SetupActivity extends DotStepper {

    public static final String SETUP_FINISHED = "setup-finished";

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

        Intent i = new Intent(this, MainActivity.class);
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
            Log.d(Config.LOG_TAG, "Got QR code from intent");
            qrFragment.getQrResult(scanResult);
            onNext();
        } else {
            Log.d(Config.LOG_TAG, "Got invalid result from activity");
        }
    }

    public void onExpertPush(View view) {
        Switch s = (Switch) view;
        LinearLayout l = (LinearLayout) findViewById(R.id.expert_options);
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
