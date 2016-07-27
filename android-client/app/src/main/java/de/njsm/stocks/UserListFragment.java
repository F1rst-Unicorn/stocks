package de.njsm.stocks;


import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.db.data.SqlUserTable;

public class UserListFragment extends ListFragment implements AbsListView.OnScrollListener {

    ListView mList;
    Cursor mCursor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        mCursor = DatabaseHandler.h.getUserCursor();
        String[] sourceName = {SqlUserTable.COL_NAME};
        int[] destIds = {android.R.id.text1};

        SimpleCursorAdapter content = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                mCursor,
                sourceName,
                destIds,
                0
        );
        setListAdapter(content);

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnScrollListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mList.setOnScrollListener(null);
        mList = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
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
        SwipeRefreshLayout swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_overlay);
        boolean enable = false;
        if(mList != null && mList.getChildCount() > 0){
            // check if the first item of the mList is visible
            boolean firstItemVisible = mList.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = mList.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        swiper.setEnabled(enable);
    }
}
