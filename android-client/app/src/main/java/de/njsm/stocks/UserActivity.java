package de.njsm.stocks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import de.njsm.stocks.adapters.IconStringAdapter;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;
import de.njsm.stocks.backend.network.NewDeviceTask;
import de.njsm.stocks.backend.network.SyncTask;

public class UserActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
                NewDeviceTask.TicketCallback,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String KEY_USER_ID = "de.njsm.stocks.UserActivity.id";
    public static final String KEY_USER_NAME = "de.njsm.stocks.UserActivity.name";

    protected int userId;
    protected String name;

    ListView list;
    protected SimpleCursorAdapter mAdapter;

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

        String[] sourceName = {SqlDeviceTable.COL_NAME};
        int[] destIds = {android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                sourceName,
                destIds,
                0
        );
        list.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    public void addDevice(View v) {
        final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_new_device))
                .setView(textField)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = textField.getText().toString();
                        NewDeviceTask task = new NewDeviceTask(UserActivity.this, UserActivity.this);
                        task.execute(name, String.valueOf(UserActivity.this.userId), UserActivity.this.name);
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


    @Override
    public void applyToTicket(String ticket) {
        Log.i(Config.log, ticket);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;

        uri = Uri.parse("content://" + StocksContentProvider.AUTHORITY + "/" + SqlDeviceTable.NAME);

        return new CursorLoader(this, uri,
                null, null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
