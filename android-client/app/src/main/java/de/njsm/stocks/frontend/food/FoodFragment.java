package de.njsm.stocks.frontend.food;

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
import android.widget.TextView;
import de.njsm.stocks.R;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlFoodItemTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.common.data.FoodItem;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.addfood.AddFoodItemActivity;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class FoodFragment extends AbstractDataFragment implements
        SimpleCursorAdapter.ViewBinder,
        AdapterView.OnItemLongClickListener{

    public static final String KEY_ID = "de.njsm.stocks.frontend.food.FoodFragment.id";

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

            LocalDate date;
            try {
                date = LocalDate.from(Config.DATABASE_DATE_FORMAT.parse(dateString));
            } catch (DateTimeParseException e) {
                date = null;
            }
            assert date != null;

            text.setText(prettyPrint(date));
            return true;
        } else {
            return false;
        }
    }

    private CharSequence prettyPrint(LocalDate date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yy");
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

        FoodItem selectedItem = new FoodItem();
        selectedItem.id = itemId;
        selectedItem.eatByDate = Instant.from(Config.DATABASE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex("date"))));
        selectedItem.storedIn = cursor.getInt(cursor.getColumnIndex("location_id"));
        cursor.moveToPosition(lastPos);

        networkManager.deleteFoodItem(selectedItem);
        Intent intent = new Intent(getActivity(), AddFoodItemActivity.class);
        intent.putExtra(AddFoodItemActivity.KEY_ID, foodId);
        intent.putExtra(AddFoodItemActivity.KEY_FOOD, ((FoodActivity) getActivity()).name);
        intent.putExtra(AddFoodItemActivity.KEY_DATE, selectedItem.eatByDate.toEpochMilli());
        intent.putExtra(AddFoodItemActivity.KEY_LOCATION, selectedItem.storedIn);
        startActivity(intent);

        return true;
    }

}
