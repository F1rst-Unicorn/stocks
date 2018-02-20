package de.njsm.stocks.frontend.addfood;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.common.data.FoodItem;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

public class EditFoodItemActivity extends AppCompatActivity {

    public static final String KEY_FOOD = "de.njsm.stocks.frontend.addfood.AddFoodItemActivity.name";

    public static final String KEY_ID = "de.njsm.stocks.frontend.addfood.AddFoodItemActivity.id";

    public static final String KEY_LOCATION = "de.njsm.stocks.frontend.addfood.AddFoodItemActivity.location";

    public static final String KEY_DATE = "de.njsm.stocks.frontend.addfood.AddFoodItemActivity.date";

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

        setTitle(String.format(getResources().getString(R.string.title_edit_item), food));

        setupLocationDataAdapter();

        spinner = findViewById(R.id.activity_add_food_item_spinner);
        spinner.setAdapter(adapter);

        picker = findViewById(R.id.activity_add_food_item_date);
        picker.setMinDate(Instant.now().toEpochMilli());
        LocalDate preselection = getSelectedDate(getIntent());
        picker.init(preselection.getYear(),
                preselection.getMonthValue()-1,
                preselection.getDayOfMonth(), null);

        Bundle args = new Bundle();
        args.putInt("id", getIntent().getExtras().getInt(KEY_LOCATION));
        getLoaderManager().initLoader(1, args, new EditDataLoader(
                (Cursor cursor) -> adapter.swapCursor(cursor),
                (Integer value) -> spinner.setSelection(value),
                this));

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
        Log.d(Config.LOG_TAG, "EditFoodItemActivity created");
    }

    private LocalDate getSelectedDate(Intent intent) {
        long date = lookupDate(intent);
        return LocalDate.from(Instant.ofEpochMilli(date).atZone(ZoneId.of("UTC")));
    }

    private long lookupDate(Intent intent) {
        if (intent.getExtras().containsKey(KEY_DATE)) {
            return intent.getExtras().getLong(KEY_DATE);
        } else {
            return Instant.now().toEpochMilli();
        }
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
    public void onBackPressed() {
        LocalDate preselection = getSelectedDate(getIntent());
        sendItem(preselection, getIntent().getExtras().getInt(KEY_LOCATION));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_add_food_item_done:
                if (addItem()) {
                    super.onBackPressed();
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
        Log.d(Config.LOG_TAG, "adding new item");
        LocalDate finalDate = readDateFromPicker();
        int locId = (int) spinner.getSelectedItemId();

        if (locId == 0) {
            Log.d(Config.LOG_TAG, "no location selected");
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
        Log.d(Config.LOG_TAG, "item sent");
        return true;
    }

    private void sendItem(LocalDate finalDate, int locId) {
        FoodItem item = new FoodItem(0,
                Instant.from(finalDate.atStartOfDay().atZone(ZoneId.of("UTC"))),
                id, locId, 0, 0);
        networkManager.addFoodItem(item);
        Toast.makeText(
                this,
                food + " " + getResources().getString(R.string.dialog_added),
                Toast.LENGTH_SHORT
        ).show();
    }

    private LocalDate readDateFromPicker() {
        return LocalDate.of(
                picker.getYear(),
                picker.getMonth()+1,
                picker.getDayOfMonth());
    }
}
