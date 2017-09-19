package de.njsm.stocks.frontend.main;


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
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.location.LocationActivity;

public class LocationListFragment extends AbstractDataFragment
        implements AdapterView.OnItemLongClickListener{

    private NetworkManager networkManager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {SqlLocationTable.COL_NAME};
        int[] destIds = {R.id.item_location_name};

        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_location,
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
        int locId = cursor.getInt(cursor.getColumnIndex(SqlLocationTable.COL_ID));
        String locName = cursor.getString(cursor.getColumnIndex(SqlLocationTable.COL_NAME));
        cursor.moveToPosition(lastPos);

        Intent i = new Intent(getActivity(), LocationActivity.class);
        i.putExtra(LocationActivity.KEY_LOCATION_ID, locId);
        i.putExtra(LocationActivity.KEY_LOCATION_NAME, locName);
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlLocationTable.NAME);

        return new CursorLoader(getActivity(), uri,
                null, null, null,
                null);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (cursor == null) {
            return true;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(i);
        final int locId = cursor.getInt(cursor.getColumnIndex(SqlLocationTable.COL_ID));
        final String locName = cursor.getString(cursor.getColumnIndex(SqlLocationTable.COL_NAME));
        cursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_delete_format),
                locName);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_delete_location))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), (DialogInterface dialog, int whichButton) -> {
                        networkManager.deleteLocation(new Location(locId, locName));
                })
                .setNegativeButton(getResources().getString(android.R.string.no), (DialogInterface dialog, int whichButton) -> {})
                .show();
        return true;
    }
}
