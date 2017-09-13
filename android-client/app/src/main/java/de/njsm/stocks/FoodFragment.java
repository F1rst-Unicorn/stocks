package de.njsm.stocks;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.FoodItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodFragment extends ListFragment implements
        AbsListView.OnScrollListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorAdapter.ViewBinder,
        AdapterView.OnItemLongClickListener{

    public static final String KEY_ID = "de.njsm.stocks.FoodFragment.id";

    protected int mFoodId;

    protected SwipeRefreshLayout mSwiper;

    protected Cursor mCursor;
    protected SimpleCursorAdapter mAdapter;
    private NetworkManager networkManager;

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

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {"date", "location", "user", "device"};
        int[] destIds = {
                R.id.item_food_item_date,
                R.id.item_food_item_location,
                R.id.item_food_item_user,
                R.id.item_food_item_device};

        mAdapter = new FoodItemCursorAdapter(
                getActivity(),
                R.layout.item_food_item,
                null,
                sourceName,
                destIds,
                0,
                R.id.item_food_item_icon
        );
        mAdapter.setViewBinder(this);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        getListView().setOnItemLongClickListener(this);

        AsyncTaskFactory factory = new AsyncTaskFactory(getActivity());
        networkManager = new NetworkManager(factory);
        factory.setNetworkManager(networkManager);

    }

    @Override
    public void onStart() {
        super.onStart();
        mSwiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.food_swipe);
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
        final int itemId = mCursor.getInt(mCursor.getColumnIndex(SqlFoodItemTable.COL_ID));
        mCursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_eat_format),
                getActivity().getTitle());
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_consume))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        networkManager.deleteFoodItem(new FoodItem(itemId, null, 0,0,0,0));
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mCursor == null) {
            return true;
        }
        int lastPos = mCursor.getPosition();
        mCursor.moveToPosition(i);
        final int itemId = mCursor.getInt(mCursor.getColumnIndex(SqlFoodItemTable.COL_ID));
        mCursor.moveToPosition(lastPos);

        final Spinner spinner = getLocationSpinner();

        String message = getResources().getString(R.string.dialog_move_item);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_move))
                .setMessage(message)
                .setView(spinner)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FoodItem i = new FoodItem(itemId, null, 0,0,0,0);
                        networkManager.moveItem(i, (int) spinner.getSelectedItemId());
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
        return true;
    }

    protected Spinner getLocationSpinner() {
        Spinner result;
        result = new Spinner(getActivity());

        String[] from = {SqlLocationTable.COL_NAME};
        int[] to = {android.R.id.text1};

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                0
        );
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        result.setAdapter(adapter);

        getLoaderManager().initLoader(1, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                Uri uri = Uri.withAppendedPath(StocksContentProvider.baseUri, SqlLocationTable.NAME);
                return new CursorLoader(FoodFragment.this.getActivity(), uri,
                        null, null, null,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.swapCursor(null);
            }
        });

        return result;
    }
}
