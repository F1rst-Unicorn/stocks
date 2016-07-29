package de.njsm.stocks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {

    public static final String KEY_LOCATION_ID = "de.njsm.stocks.LocationActivity.id";
    public static final String KEY_LOCATION_NAME = "de.njsm.stocks.LocationActivity.name";

    protected String mLocation;
    protected int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocation = getIntent().getExtras().getString(KEY_LOCATION_NAME);
        mId = getIntent().getExtras().getInt(KEY_LOCATION_ID);

        setTitle(mLocation);




    }

}
