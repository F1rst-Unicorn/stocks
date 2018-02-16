package de.njsm.stocks.frontend.addfood;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlLocationTable;
import de.njsm.stocks.backend.util.Config;

import java.util.function.Consumer;

class EditDataLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Activity context;

    private Consumer<Cursor> listener;

    private Consumer<Integer> selector;

    private Bundle args;

    EditDataLoader(Consumer<Cursor> listener, Consumer<Integer> selector, Activity context) {
        this.listener = listener;
        this.context = context;
        this.selector = selector;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        switch(id) {
            case 1:
                this.args = args;
                uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, SqlLocationTable.NAME);
                Log.d(Config.LOG_TAG, "loader for locations started");
                return new CursorLoader(context, uri,
                        null, null, null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case 1:
                Log.d(Config.LOG_TAG, "locations loaded");
                int idToFind = args.getInt("id");
                int colId = data.getColumnIndex("_id");
                int position = 0;
                data.moveToFirst();
                do {
                    if (idToFind == data.getInt(colId)) {
                        break;
                    }
                    position++;
                } while (data.moveToNext());
                Log.d(Config.LOG_TAG, "location id: " + idToFind + ", position: " + position);
                data.moveToFirst();
                listener.accept(data);
                selector.accept(position);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case 1:
                listener.accept(null);
                Log.d(Config.LOG_TAG, "resetted locations");
                break;
        }
    }
}
