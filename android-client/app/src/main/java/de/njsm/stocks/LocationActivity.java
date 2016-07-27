package de.njsm.stocks;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class LocationActivity extends ListActivity {

    public static final String KEY_LOCATION_ID = "de.njsm.stocks.LocationActivity.id";
    public static final String KEY_LOCATION_NAME = "de.njsm.stocks.LocationActivity.name";

    Food[] food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getExtras().getString(KEY_LOCATION_NAME));

        new Thread(new Runnable() {
            @Override
            public void run() {
                food = DatabaseHandler.h.getFood();
                String[] names = new String[food.length];
                for (int i = 0; i < food.length; i++){
                    names[i] = food[i].name;
                }

                final ListAdapter content = new ArrayAdapter<>(LocationActivity.this,
                        android.R.layout.simple_list_item_1,
                        names);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListAdapter(content);
                    }
                });
            }
        }).start();


    }

}
