package de.njsm.stocks.frontend.barcodes;


import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlEanNumberTable;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.common.data.EanNumber;
import de.njsm.stocks.frontend.AbstractDataFragment;

public class BarcodeFragment extends AbstractDataFragment
        implements AdapterView.OnItemLongClickListener {

    private ListView mList;

    private int foodId;

    private NetworkManager networkManager;

    public static BarcodeFragment newInstance(int foodId) {
        BarcodeFragment result = new BarcodeFragment();
        Bundle args = new Bundle();
        args.putInt("id", foodId);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foodId = getArguments().getInt("id");

        String[] sourceName = {SqlEanNumberTable.COL_NUMBER};
        int[] destIds = {android.R.id.text1};

        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null, sourceName, destIds, 0);
        setListAdapter(adapter);
        getLoaderManager().initLoader(1, null, this);

        AsyncTaskFactory factory = new AsyncTaskFactory(getActivity());
        networkManager = new NetworkManager(factory);
    }

    @Override
    public void onStart() {
        super.onStart();
        mList = getListView();
        mList.setOnItemLongClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mList.setOnItemLongClickListener(null);
        mList = null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        if (cursor == null) {
            return true;
        }
        int lastPos = cursor.getPosition();
        cursor.moveToPosition(i);
        final int numberId = cursor.getInt(cursor.getColumnIndex(SqlEanNumberTable.COL_ID));
        final String number = cursor.getString(cursor.getColumnIndex(SqlEanNumberTable.COL_NUMBER));
        cursor.moveToPosition(lastPos);

        String message = String.format(getResources().getString(R.string.dialog_delete_format),
                number);
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.title_delete_barcode))
                .setMessage(message)
                .setPositiveButton(getResources().getString(android.R.string.yes), (DialogInterface dialog, int whichButton) ->
                    networkManager.deleteEanNumber(new EanNumber(numberId, number, 0)))
                .setNegativeButton(getResources().getString(android.R.string.no), (DialogInterface dialog, int whichButton) -> {})
                .show();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(
                StocksContentProvider.BASE_URI,
                StocksContentProvider.EAN_NUMBER_TYPE);

        return new CursorLoader(
                getActivity(),
                uri,
                null, null,
                new String[] {String.valueOf(foodId)},
                null);
    }
}
