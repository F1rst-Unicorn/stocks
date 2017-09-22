package de.njsm.stocks.frontend.search;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.frontend.AbstractDataFragment;
import de.njsm.stocks.frontend.util.DateViewBinder;

public class SearchFragment extends AbstractDataFragment {

    public static final String KEY_QUERY = "de.njsm.stocks.frontend.search.SearchFragment.query";

    private String query;

    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        query = getArguments().getString(KEY_QUERY, "");
        swiper = (SwipeRefreshLayout) getActivity().findViewById(R.id.location_swipe);

        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = new Bundle();
        args.putString("query", query);
        getLoaderManager().initLoader(0, args, this);

        setupDataAdapter();
    }

    private void setupDataAdapter() {
        String[] sourceName = {"name", "amount"};
        int[] destIds = {R.id.item_food_amount_name, R.id.item_food_amount_amount};
        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_food_amount,
                null,
                sourceName,
                destIds,
                0
        );
        adapter.setViewBinder(new DateViewBinder());
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.BASE_URI,
                StocksContentProvider.SEARCH_FOOD);
        String queryForSql = "%" + query + "%";
        String[] selectionArgs = new String[] {
                queryForSql, queryForSql
        };

        return new CursorLoader(
                getActivity(),
                uri,
                null,
                null,
                selectionArgs,
                null);
    }
}
