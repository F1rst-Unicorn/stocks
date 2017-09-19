package de.njsm.stocks.frontend.addfood;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.db.data.SqlLocationTable;

import java.util.function.Consumer;

class DataLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Activity context;

    private Consumer<Cursor> listener;

    private Consumer<Integer> selector;

    private Bundle args;

    DataLoader(Consumer<Cursor> listener, Consumer<Integer> selector, Activity context) {
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
                return new CursorLoader(context, uri,
                        null, null, null,
                        null);
            case 2:
                uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI, StocksContentProvider.MAX_LOCATION);
                return new CursorLoader(context, uri,
                        null, null, new String[] {String.valueOf(this.args.getInt("id"))},
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case 1:
                listener.accept(cursor);
                context.getLoaderManager().initLoader(2, null, this);
                break;
            case 2:
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    int idToFind = cursor.getInt(cursor.getColumnIndex("_id"));
                    int colId = cursor.getColumnIndex("_id");
                    int position = 0;
                    cursor.moveToFirst();
                    do {
                        if (idToFind == cursor.getInt(colId)) {
                            break;
                        }
                        position++;
                    } while (cursor.moveToNext());
                    selector.accept(position);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case 1:
                listener.accept(null);
                break;
            case 2:
                break;
        }
    }
}
