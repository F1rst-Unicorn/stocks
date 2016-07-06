package de.njsm.stocks;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import de.njsm.stocks.adapters.IconStringAdapter;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class UserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String KEY_USER_ID = "de.njsm.stocks.UserActivity.id";
    public static final String KEY_USER_NAME = "de.njsm.stocks.UserActivity.name";

    protected int userId;
    protected String name;
    ListView list;
    UserDevice[] devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        userId = getIntent().getExtras().getInt(KEY_USER_ID);
        name = getIntent().getExtras().getString(KEY_USER_NAME);

        TextView tv = (TextView) findViewById(R.id.user_detail_name);
        assert tv != null;
        tv.setText(name);
        list = (ListView) findViewById(R.id.user_detail_device_list);

        new Thread(new Runnable() {
            @Override
            public void run() {
                devices = DatabaseHandler.h.getDevices(userId);
                String[] deviceNames = new String[devices.length];
                for (int i = 0; i < devices.length; i++){
                    deviceNames[i] = devices[i].name;
                }

                final ListAdapter content = new ArrayAdapter<>(getThis(),
                        android.R.layout.simple_list_item_1,
                        deviceNames);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.setAdapter(content);
                    }
                });
            }
        }).start();
    }

    public void addDevice(View v) {
        final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_new_device))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = textField.getText().toString();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view == list) {


        }
    }

    public Context getThis() {
        return this;
    }


}
