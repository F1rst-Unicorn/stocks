package de.njsm.stocks.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.github.fcannizzaro.materialstepper.style.DotStepper;

import de.njsm.stocks.MainActivity;
import de.njsm.stocks.R;

public class SetupActivity extends DotStepper {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle("Connection Setup");

        addStep(new ServerFragment());
        addStep(new PrincipalsFragment());
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onComplete(Bundle data) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
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
