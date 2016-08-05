package de.njsm.stocks;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.njsm.stocks.backend.db.StocksContentProvider;

public class EmptyFoodFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.OnScrollListener{

    protected SwipeRefreshLayout mSwiper;

    protected SimpleCursorAdapter mAdapter;
    protected Cursor mCursor;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] from = {"name"};
        int[] to = {R.id.item_empty_food_outline_name};

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_empty_food_outline,
                null,
                from,
                to,
                0
        );
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mSwiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.empty_food_swipe);
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getListView().setOnScrollListener(null);
        mSwiper = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCursor == null) {
            return;
        }
        int lastPos = mCursor.getPosition();
        mCursor.moveToPosition(position);
        int foodId = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String name = mCursor.getString(mCursor.getColumnIndex("name"));
        mCursor.moveToPosition(lastPos);

        Intent i = new Intent(getActivity(), FoodActivity.class);
        i.putExtra(FoodActivity.KEY_ID, foodId);
        i.putExtra(FoodActivity.KEY_NAME, name);
        startActivity(i);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.baseUri,
                StocksContentProvider.emptyFood);

        return new CursorLoader(
                getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mAdapter.swapCursor(null);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        boolean enable = false;
        ListView list = getListView();
        if(list != null && list.getChildCount() > 0){
            // check if the first item of the mList is visible
            boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        mSwiper.setEnabled(enable);
    }
}
