package de.njsm.stocks.frontend.eatsoon;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import de.njsm.stocks.R;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.food.FoodActivity;
import de.njsm.stocks.frontend.util.DateViewBinder;

public class EatSoonFragment extends AbstractDataFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupDataAdapter();

        getLoaderManager().initLoader(0, null, this);
    }

    private void setupDataAdapter() {
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
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.eat_soon_swipe);
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
                StocksContentProvider.EAT_SOON);

        return new CursorLoader(
                getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }
}
