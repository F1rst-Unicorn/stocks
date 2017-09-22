package de.njsm.stocks.frontend;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public abstract class AbstractDataFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.OnScrollListener {

    protected ListView list;
    protected SwipeRefreshLayout swiper;

    protected Cursor cursor;
    protected SimpleCursorAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        list = getListView();
        list.setOnScrollListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        list.setOnScrollListener(null);
        list = null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        cursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        cursor = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        boolean enable = false;
        if(list != null && list.getChildCount() > 0){
            // check if the first item of the mList is visible
            boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        if (swiper != null) {
            swiper.setEnabled(enable);
        }
    }
}
