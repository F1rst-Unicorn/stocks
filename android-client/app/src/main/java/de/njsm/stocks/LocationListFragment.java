package de.njsm.stocks;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.DeleteLocationTask;
import de.njsm.stocks.common.data.Location;

public class LocationListFragment extends ListFragment
        implements AbsListView.OnScrollListener,
                   LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemLongClickListener{

    protected ListView mList;
    protected SwipeRefreshLayout mSwiper;

    protected Cursor mCursor;
    protected SimpleCursorAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        mSwiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {SqlLocationTable.COL_NAME};
        int[] destIds = {R.id.item_location_name};

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_location,
                null,
                sourceName,
                destIds,
                0
        );

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnScrollListener(this);
        mList.setOnItemLongClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mList.setOnScrollListener(null);
        mList.setOnItemLongClickListener(null);
        mList = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCursor == null) {
            return;
        }
        int lastPos = mCursor.getPosition();
        mCursor.moveToPosition(position);
        int locId = mCursor.getInt(mCursor.getColumnIndex(SqlLocationTable.COL_ID));
        String locName = mCursor.getString(mCursor.getColumnIndex(SqlLocationTable.COL_NAME));
        mCursor.moveToPosition(lastPos);

        Intent i = new Intent(getActivity(), LocationActivity.class);
        i.putExtra(LocationActivity.KEY_LOCATION_ID, locId);
        i.putExtra(LocationActivity.KEY_LOCATION_NAME, locName);
        startActivity(i);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        boolean enable = false;
        if(mList != null && mList.getChildCount() > 0){
            // check if the first item of the mList is visible
            boolean firstItemVisible = mList.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = mList.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        mSwiper.setEnabled(enable);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(StocksContentProvider.baseUri, SqlLocationTable.NAME);

        return new CursorLoader(getActivity(), uri,
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mCursor == null) {
            return true;
        }
        int lastPos = mCursor.getPosition();
        mCursor.moveToPosition(i);
        final int locId = mCursor.getInt(mCursor.getColumnIndex(SqlLocationTable.COL_ID));
        final String locName = mCursor.getString(mCursor.getColumnIndex(SqlLocationTable.COL_NAME));
        mCursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_delete_format),
                locName);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_delete_location))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeleteLocationTask task = new DeleteLocationTask(getActivity());
                        task.execute(new Location(locId, locName));
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
        return true;
    }
}
