package de.njsm.stocks.frontend.search;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import de.njsm.stocks.R;
import de.njsm.stocks.adapters.FoodItemCursorAdapter;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.frontend.util.DateViewBinder;

public class SearchActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final ComponentName NAME = new ComponentName("de.njsm.stocks.frontend.search",
            "SearchActivity");

    private Cursor cursor;

    private FoodItemCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDataAdapter();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }

    private void search(String query) {
        Bundle args = new Bundle();
        args.putString("query", query);
        getLoaderManager().initLoader(0, args, this);
    }

    private void setupDataAdapter() {
        String[] sourceName = {"name", "amount", "date"};
        int[] destIds = {R.id.item_food_outline_name, R.id.item_food_outline_count, R.id.item_food_outline_date};
        adapter = new FoodItemCursorAdapter(
                this,
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.BASE_URI,
                StocksContentProvider.SEARCH_FOOD);
        String query = "%" + args.getString("query") + "%";
        String[] selectionArgs = new String[] {
                query, query
        };

        return new CursorLoader(
                this,
                uri,
                null,
                null,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        cursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
        adapter.swapCursor(null);
    }
}
