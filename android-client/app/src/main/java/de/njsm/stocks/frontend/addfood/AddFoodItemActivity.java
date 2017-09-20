package de.njsm.stocks.frontend.addfood;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.FoodItem;

import java.util.Calendar;
import java.util.Date;

public class AddFoodItemActivity extends AppCompatActivity {

    public static final String KEY_FOOD = "de.njsm.stocks.frontend.addfood.AddFoodItemActivity.name";

    public static final String KEY_ID = "de.njsm.stocks.frontend.addfood.AddFoodItemActivity.id";

    private String food;
    private int id;

    private DatePicker picker;
    private Spinner spinner;

    private SimpleCursorAdapter adapter;

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

        Bundle args = new Bundle();
        args.putInt("id", id);
        getLoaderManager().initLoader(1, args, new DataLoader(
                (Cursor cursor) -> adapter.swapCursor(cursor),
                (Integer value) -> spinner.setSelection(value),
                this));

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_food_item_menu, menu);
        return true;
    }

    private void setupLocationDataAdapter() {
        String[] from = {SqlLocationTable.COL_NAME};
        int[] to = {R.id.item_location_name};

        adapter = new SimpleCursorAdapter(this,
                R.layout.item_location,
                null, from, to, 0);
        adapter.setDropDownViewResource(R.layout.item_location);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_add_food_item_done:
                if (addItem()) {
                    onBackPressed();
                }
                break;
            case R.id.activity_add_food_item_add_more:
                addItem();
                break;
            default:
        }
        return true;
    }

    private boolean addItem() {
        Date finalDate = readDateFromPicker();
        int locId = (int) spinner.getSelectedItemId();

        if (locId == 0) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_error))
                    .setMessage(getResources().getString(R.string.error_no_location_present))
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {})
                    .create()
                    .show();
            return false;
        }

        sendItem(finalDate, locId);
        return true;
    }

    private void sendItem(Date finalDate, int locId) {
        FoodItem item = new FoodItem(0,
                finalDate,
                id, locId, 0, 0);
        networkManager.addFoodItem(item);
        Toast.makeText(
                this,
                food + " " + getResources().getString(R.string.dialog_added),
                Toast.LENGTH_SHORT
        ).show();
    }

    private Date readDateFromPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                picker.getYear(),
                picker.getMonth(),
                picker.getDayOfMonth());
        return calendar.getTime();
    }
}
