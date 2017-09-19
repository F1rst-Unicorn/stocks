package de.njsm.stocks.frontend.location;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.njsm.stocks.R;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.food.FoodActivity;
import de.njsm.stocks.frontend.util.DateViewBinder;

public class FoodListFragment extends AbstractDataFragment {

    public static final String KEY_ID = "de.njsm.stocks.frontend.location.FoodListFragment.id";

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
        adapter.setViewBinder(new DateViewBinder());
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
}
