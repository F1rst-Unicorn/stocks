package de.njsm.stocks.frontend.barcodes;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.common.data.EanNumber;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

public class BarcodeActivity extends AppCompatActivity {

    public static final String KEY_ID = "de.njsm.stocks.frontend.barcodes.BarcodeActivity.id";

    public static final String KEY_NAME = "de.njsm.stocks.frontend.barcodes.BarcodeActivity.name";

    private int foodId;

    private String foodName;

    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        foodId = getIntent().getExtras().getInt(KEY_ID);
        foodName = getIntent().getExtras().getString(KEY_NAME);

        setTitle(String.format(getResources().getString(R.string.title_barcode_activity), foodName));

        Fragment listFragment = BarcodeFragment.newInstance(foodId);
        getFragmentManager().beginTransaction()
                .replace(R.id.barcode_content, listFragment)
                .commit();

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            networkManager.addEanNumber(new EanNumber(0, scanResult.getContents(), foodId));
        } else {
            Log.d(Config.LOG_TAG, "Got invalid result from activity");
        }
    }

    public void addBarcode(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }
}
