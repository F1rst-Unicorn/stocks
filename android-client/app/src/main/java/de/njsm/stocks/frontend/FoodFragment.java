package de.njsm.stocks.frontend;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.*;
import de.njsm.stocks.R;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.common.data.FoodItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodFragment extends AbstractDataFragment implements
        SimpleCursorAdapter.ViewBinder,
        AdapterView.OnItemLongClickListener{

    public static final String KEY_ID = "de.njsm.stocks.frontend.FoodFragment.id";

    private int foodId;

    private NetworkManager networkManager;

    public static FoodFragment newInstance(int aFoodId) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID, aFoodId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.food_swipe);
        foodId = getArguments().getInt(KEY_ID, 0);
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

        adapter = new FoodItemCursorAdapter(
                getActivity(),
                R.layout.item_food_item,
                null,
                sourceName,
                destIds,
                0,
                R.id.item_food_item_icon
        );
        adapter.setViewBinder(this);

        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        getListView().setOnItemLongClickListener(this);

        AsyncTaskFactory factory = new AsyncTaskFactory(getActivity());
        networkManager = new NetworkManager(factory);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (cursor == null) {
            return;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(position);
        final int itemId = cursor.getInt(cursor.getColumnIndex(SqlFoodItemTable.COL_ID));
        cursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_eat_format),
                getActivity().getTitle());
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_consume))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), (DialogInterface dialog, int whichButton) -> {
                        networkManager.deleteFoodItem(new FoodItem(itemId, null, 0,0,0,0));
                })
                .setNegativeButton(getResources().getString(android.R.string.no), (DialogInterface dialog, int whichButton) -> {})
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.BASE_URI,
                StocksContentProvider.FOOD_ITEM_TYPE);

        return new CursorLoader(
                getActivity(),
                uri,
                null, null,
                new String[] {String.valueOf(foodId)},
                null);

    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex("date")) {
            TextView text = (TextView) view;
            String dateString = cursor.getString(columnIndex);

            Date date;
            try {
                date = Config.DATABASE_DATE_FORMAT.parse(dateString);
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
        if (cursor == null) {
            return true;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(i);
        final int itemId = cursor.getInt(cursor.getColumnIndex(SqlFoodItemTable.COL_ID));
        cursor.moveToPosition(lastPos);

        final Spinner spinner = getLocationSpinner();

        String message = getResources().getString(R.string.dialog_move_item);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_move))
                .setMessage(message)
                .setView(spinner)
                .setPositiveButton(getResources().getString(android.R.string.yes), (DialogInterface dialog, int whichButton) -> {
                        FoodItem item = new FoodItem(itemId, null, 0,0,0,0);
                        networkManager.moveItem(item, (int) spinner.getSelectedItemId());
                })
                .setNegativeButton(getResources().getString(android.R.string.no), (DialogInterface dialog, int whichButton) -> {})
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
                Uri uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlLocationTable.NAME);
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
