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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;

public class FoodFragment extends ListFragment implements
        AbsListView.OnScrollListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorAdapter.ViewBinder{

    public static final String KEY_ID = "de.njsm.stocks.FoodFragment.id";

    protected int mFoodId;

    protected ListView mList;
    protected SwipeRefreshLayout mSwiper;

    protected Cursor mCursor;
    protected SimpleCursorAdapter mAdapter;

    public static FoodFragment newInstance(int aFoodId) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID, aFoodId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        mFoodId = getArguments().getInt(KEY_ID, 0);
        mSwiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.food_swipe);

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {"date", "location", "user", "device"};
        int[] destIds = {
                R.id.item_item_date,
                R.id.item_item_location,
                R.id.item_item_user,
                R.id.item_item_device};

        mAdapter = new FoodItemCursorAdapter(
                getActivity(),
                R.layout.food_item_list_item,
                null,
                sourceName,
                destIds,
                0,
                R.id.item_item_icon
        );
        mAdapter.setViewBinder(this);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
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
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.baseUri,
                StocksContentProvider.foodItemType);

        return new CursorLoader(
                getActivity(),
                uri,
                null, null,
                new String[] {String.valueOf(mFoodId)},
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
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex("date")) {
            TextView text = (TextView) view;
            String dateString = cursor.getString(columnIndex);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

            Date date;
            try {
                date = format.parse(dateString);
            } catch (ParseException e) {
                date = null;
            }
            assert date != null;

            text.setText(prettyPrint(date));
            return true;
        } else {
            return false;
        }
    }

    private CharSequence prettyPrint(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy", Locale.US);
        return format.format(date);
    }
}
