package de.njsm.stocks.backend.network;

import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import de.njsm.stocks.Config;
import de.njsm.stocks.UserListFragment;
import de.njsm.stocks.backend.data.Food;
import de.njsm.stocks.backend.data.FoodItem;
import de.njsm.stocks.backend.data.Location;
import de.njsm.stocks.backend.data.Update;
import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.data.UserDevice;
import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.db.data.SqlDeviceTable;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlFoodTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class NewUserTask extends AsyncTask<String, Void, Integer> {

    public UserListFragment fragment;

    public NewUserTask(Fragment usersFragment) {
        if (! (usersFragment instanceof UserListFragment)) {
            throw new RuntimeException("Fragment is not UserListFragment");
        }

        fragment = (UserListFragment) usersFragment;

    }

    @Override
    protected Integer doInBackground(String... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        User user = new User(0, params[0]);
        ServerManager.m.addUser(user);

        return 0;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Integer integer) {
    }
}

