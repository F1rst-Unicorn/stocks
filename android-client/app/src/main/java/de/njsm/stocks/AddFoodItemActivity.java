package de.njsm.stocks;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.NewFoodItemTask;

public class AddFoodItemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String KEY_FOOD = "de.njsm.stocks.AddFoodItemActivity.name";
    public static final String KEY_ID = "de.njsm.stocks.AddFoodItemActivity.id";

    protected String mFood;
    protected int mId;

    protected SimpleCursorAdapter mAdapter;
    protected Cursor mCursor;

    protected Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        Bundle extras = getIntent().getExtras();
        mFood = extras.getString(KEY_FOOD);
        mId = extras.getInt(KEY_ID);

        setTitle(mFood);

        String[] from = {SqlLocationTable.COL_NAME};
        int[] to = {R.id.item_location_name};

        mAdapter = new SimpleCursorAdapter(
                this,
                R.layout.item_location,
                null,
                from,
                to,
                0
        );

        mAdapter.setDropDownViewResource(R.layout.item_location);

        mSpinner = (Spinner) findViewById(R.id.activity_add_food_item_spinner);
        mSpinner.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_food_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_add_food_item_done:
                addItem();
                onBackPressed();
                break;
            case R.id.activity_add_food_item_add_more:
                addItem();
                break;
            default:
        }
        return true;
    }

    private void addItem() {
        DatePicker picker = (DatePicker) findViewById(R.id.activity_add_food_item_date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                picker.getYear(),
                picker.getMonth(),
                picker.getDayOfMonth());
        int locId = (int) mSpinner.getSelectedItemId();

        FoodItem item = new FoodItem(
                0,
                calendar.getTime(),
                mId,
                locId,
                0,
                0);
        (new NewFoodItemTask(this)).execute(item);
        Toast.makeText(
                this,
                mFood + " " + getResources().getString(R.string.dialog_added),
                Toast.LENGTH_SHORT
        ).show();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(StocksContentProvider.baseUri, SqlLocationTable.NAME);

        return new CursorLoader(this, uri,
                null, null, null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        mCursor = null;
    }
}
