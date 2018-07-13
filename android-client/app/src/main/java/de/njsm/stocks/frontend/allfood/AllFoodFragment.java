package de.njsm.stocks.frontend.allfood;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.EanNumber;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.ActivitySwitcher;
import de.njsm.stocks.frontend.food.FoodFragment;

public class AllFoodFragment extends AbstractDataFragment {

    public static final String KEY_EAN = "de.njsm.stocks.frontend.allfood.AllFoodFragment.ean";

    NetworkManager networkManager;

    String ean;

    public static FoodFragment newInstance(String aFoodId) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putString(KEY_EAN, aFoodId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupDataAdapter();
        getLoaderManager().initLoader(0, null, this);

        AsyncTaskFactory factory = new AsyncTaskFactory(getActivity());
        networkManager = new NetworkManager(factory);
    }

    private void setupDataAdapter() {
        String[] from = {"name"};
        int[] to = {android.R.id.text1};

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                0
        );
        setListAdapter(adapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.BASE_URI,
                StocksContentProvider.ALL_FOOD);

        return new CursorLoader(
                getActivity(),
                uri,
                null, null, null, null);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swiper = getActivity().findViewById(R.id.all_food_swipe);
        ean = getArguments().getString(KEY_EAN);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(position);
        int foodId = cursor.getInt(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        cursor.moveToPosition(lastPos);
        Food food = new Food(foodId, name);
        EanNumber eanNumber = new EanNumber(0, ean, foodId);
        networkManager.addEanNumber(eanNumber);
        ActivitySwitcher.switchToFoodActivity(getActivity(), food);
    }



}
