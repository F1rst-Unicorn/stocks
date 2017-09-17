package de.njsm.stocks.frontend;

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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.njsm.stocks.R;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.util.Config;

import java.text.ParseException;
import java.util.Date;

public class FoodListFragment extends AbstractDataFragment implements
        SimpleCursorAdapter.ViewBinder {

    public static final String KEY_ID = "de.njsm.stocks.frontend.FoodListFragment.id";

    private int mLocationId;

    public static FoodListFragment newInstance(int aLocationId) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID, aLocationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        mLocationId = getArguments().getInt(KEY_ID, 0);
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.location_swipe);

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] sourceName = {"name", "amount", "date"};
        int[] destIds = {R.id.item_food_outline_name, R.id.item_food_outline_count, R.id.item_food_outline_date};

        adapter = new FoodItemCursorAdapter(
                getActivity(),
                R.layout.item_food_outline,
                null,
                sourceName,
                destIds,
                0,
                R.id.item_food_outline_icon
        );
        adapter.setViewBinder(this);
        getLoaderManager().initLoader(0, null, this);
        setListAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (cursor == null) {
            return;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(position);
        int foodId = cursor.getInt(cursor.getColumnIndex("food_id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        cursor.moveToPosition(lastPos);

        Intent i = new Intent(getActivity(), FoodActivity.class);
        i.putExtra(FoodActivity.KEY_ID, foodId);
        i.putExtra(FoodActivity.KEY_NAME, name);
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.BASE_URI,
                StocksContentProvider.FOOD_ITEM_LOCATION);

        return new CursorLoader(
                getActivity(),
                uri,
                null, null,
                new String[] {String.valueOf(mLocationId)},
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
        Date now = new Date();
        return DateUtils.getRelativeTimeSpanString(date.getTime(), now.getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL);
    }
}
