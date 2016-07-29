package de.njsm.stocks;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import de.njsm.stocks.backend.data.User;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlUserTable;
import de.njsm.stocks.backend.network.DeleteUserTask;

public class UserListFragment extends ListFragment
        implements AbsListView.OnScrollListener,
                   AdapterView.OnItemLongClickListener,
                   LoaderManager.LoaderCallbacks<Cursor> {

    protected SimpleCursorAdapter mAdapter;
    protected Cursor mCursor;

    protected SwipeRefreshLayout mSwiper;
    protected ListView mList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        mSwiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {SqlUserTable.COL_NAME};
        int[] destIds = {R.id.item_name};

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.icon_list_item,
                null,
                sourceName,
                destIds,
                0
        );

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(StocksContentProvider.baseUri, SqlUserTable.NAME);

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
        int userId = mCursor.getInt(mCursor.getColumnIndex(SqlUserTable.COL_ID));
        String username = mCursor.getString(mCursor.getColumnIndex(SqlUserTable.COL_NAME));
        mCursor.moveToPosition(lastPos);

        Intent i = new Intent(getActivity(), UserActivity.class);
        i.putExtra(UserActivity.KEY_USER_ID, userId);
        i.putExtra(UserActivity.KEY_USER_NAME, username);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (mCursor == null) {
            return true;
        }
        int lastPos = mCursor.getPosition();
        mCursor.moveToPosition(position);
        final int userId = mCursor.getInt(mCursor.getColumnIndex(SqlUserTable.COL_ID));
        final String username = mCursor.getString(mCursor.getColumnIndex(SqlUserTable.COL_NAME));
        mCursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_delete_format),
                username);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_delete_user))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeleteUserTask task = new DeleteUserTask(getActivity());
                        task.execute(new User(userId, username));
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
        return true;
    }
}
