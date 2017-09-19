package de.njsm.stocks.frontend;


import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlUserTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.User;

public class UserListFragment extends AbstractDataFragment
        implements AdapterView.OnItemLongClickListener {

    private NetworkManager networkManager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {SqlUserTable.COL_NAME};
        int[] destIds = {R.id.item_user_name};

        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_user,
                null,
                sourceName,
                destIds,
                0
        );

        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        AsyncTaskFactory factory = new AsyncTaskFactory(getActivity());
        networkManager = new NetworkManager(factory);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlUserTable.NAME);

        return new CursorLoader(getActivity(), uri,
                null, null, null,
                null);

    }

    @Override
    public void onStart() {
        super.onStart();
        list.setOnItemLongClickListener(this);
    }

    @Override
    public void onStop() {
        list.setOnItemLongClickListener(null);
        super.onStop();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (cursor == null) {
            return;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(position);
        int userId = cursor.getInt(cursor.getColumnIndex(SqlUserTable.COL_ID));
        String username = cursor.getString(cursor.getColumnIndex(SqlUserTable.COL_NAME));
        cursor.moveToPosition(lastPos);

        Intent i = new Intent(getActivity(), UserActivity.class);
        i.putExtra(UserActivity.KEY_USER_ID, userId);
        i.putExtra(UserActivity.KEY_USER_NAME, username);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (cursor == null) {
            return true;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(position);
        final int userId = cursor.getInt(cursor.getColumnIndex(SqlUserTable.COL_ID));
        final String username = cursor.getString(cursor.getColumnIndex(SqlUserTable.COL_NAME));
        cursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_delete_format),
                username);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_delete_user))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        networkManager.deleteUser(new User(userId, username));
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
        return true;
    }
}
