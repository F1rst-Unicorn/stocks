package de.njsm.stocks.frontend.emptyfood;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.food.FoodActivity;

public class EmptyFoodFragment extends AbstractDataFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupDataAdapter();
        getLoaderManager().initLoader(0, null, this);
    }

    private void setupDataAdapter() {
        String[] from = {"name"};
        int[] to = {R.id.item_empty_food_outline_name};

        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_empty_food_outline,
                null,
                from,
                to,
                0
        );
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.empty_food_swipe);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (cursor == null) {
            return;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(position);
        int foodId = cursor.getInt(cursor.getColumnIndex("_id"));
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
                StocksContentProvider.EMPTY_FOOD);

        return new CursorLoader(
                getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }
}
