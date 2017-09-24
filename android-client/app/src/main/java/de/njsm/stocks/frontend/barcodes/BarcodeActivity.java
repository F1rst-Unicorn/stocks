package de.njsm.stocks.frontend.barcodes;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import de.njsm.stocks.R;

public class BarcodeActivity extends AppCompatActivity {

    public static final String KEY_ID = "de.njsm.stocks.frontend.barcodes.BarcodeActivity.id";

    public static final String KEY_NAME = "de.njsm.stocks.frontend.barcodes.BarcodeActivity.name";

    private int foodId;

    private String foodName;

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
    }

    public void addBarcode(View view) {
    }
}
