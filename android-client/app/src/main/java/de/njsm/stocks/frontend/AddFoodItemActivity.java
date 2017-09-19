package de.njsm.stocks.frontend;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.FoodItem;

import java.util.Calendar;
import java.util.Date;

public class AddFoodItemActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_FOOD = "de.njsm.stocks.frontend.AddFoodItemActivity.name";

    public static final String KEY_ID = "de.njsm.stocks.frontend.AddFoodItemActivity.id";

    private String food;
    private int id;

    private DatePicker picker;
    private Spinner spinner;

    private SimpleCursorAdapter adapter;
    private Cursor cursor;

    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        Bundle extras = getIntent().getExtras();
        food = extras.getString(KEY_FOOD);
        id = extras.getInt(KEY_ID);

        setTitle(String.format(getResources().getString(R.string.title_add_item), food));

        setupLocationDataAdapter();

        spinner = (Spinner) findViewById(R.id.activity_add_food_item_spinner);
        spinner.setAdapter(adapter);

        picker = (DatePicker) findViewById(R.id.activity_add_food_item_date);
        picker.setMinDate((new Date()).getTime());

        getLoaderManager().initLoader(1, null, this);
        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void setupLocationDataAdapter() {
        String[] from = {SqlLocationTable.COL_NAME};
        int[] to = {R.id.item_location_name};

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_location,
                null,
                from,
                to,
                0
        );

        adapter.setDropDownViewResource(R.layout.item_location);
    }

    private void addItem() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                picker.getYear(),
                picker.getMonth(),
                picker.getDayOfMonth());
        int locId = (int) spinner.getSelectedItemId();

        FoodItem item = new FoodItem(
                0,
                calendar.getTime(),
                id,
                locId,
                0,
                0);
        networkManager.addFoodItem(item);
        Toast.makeText(
                this,
                food + " " + getResources().getString(R.string.dialog_added),
                Toast.LENGTH_SHORT
        ).show();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        switch(id) {
            case 1:
                uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlLocationTable.NAME);
                return new CursorLoader(this, uri,
                        null, null, null,
                        null);
            case 2:
                uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, StocksContentProvider.MAX_LOCATION);
                return new CursorLoader(this, uri,
                        null, null, new String[] {String.valueOf(this.id)},
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case 1:
                adapter.swapCursor(data);
                cursor = data;
                getLoaderManager().initLoader(2, null, this);
                break;
            case 2:
                if (data.getCount() > 0) {
                    data.moveToFirst();
                    int idToFind = data.getInt(data.getColumnIndex("_id"));
                    int colId = cursor.getColumnIndex("_id");
                    int position = 0;
                    cursor.moveToFirst();
                    do {
                        if (idToFind == cursor.getInt(colId)) {
                            break;
                        }
                        position++;
                    } while (cursor.moveToNext());
                    spinner.setSelection(position);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case 1:
                adapter.swapCursor(null);
                cursor = null;
                break;
            case 2:
                break;
        }
    }
}
