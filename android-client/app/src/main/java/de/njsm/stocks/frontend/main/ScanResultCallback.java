package de.njsm.stocks.frontend.main;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import de.njsm.stocks.backend.db.StocksContentProvider;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.common.data.Food;

import java.util.function.Consumer;

public class ScanResultCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_EAN_NUMBER = "de.njsm.stocks.frontend.main.ScanResultCallback.eanNumber";

    private Consumer<Food> listener;

    private Consumer<Void> failureListener;

    private Context context;

    public ScanResultCallback(Context context,
                              Consumer<Food> listener,
                              Consumer<Void> failureListener) {
        this.context = context;
        this.listener = listener;
        this.failureListener = failureListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(Config.LOG_TAG, "Loading food for EAN number " + args.getString(KEY_EAN_NUMBER));
        Uri uri = Uri.withAppendedPath(StocksContentProvider.BASE_URI,
                StocksContentProvider.EAN_NUMBER_TO_FOOD);

        return new CursorLoader(
                context,
                uri,
                null, null,
                new String[] {args.getString(KEY_EAN_NUMBER)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(Config.LOG_TAG, "Loading finished");
        if (data != null && data.getCount() >= 1) {
            data.moveToFirst();
            Food result = new Food(
                    data.getInt(data.getColumnIndex("_id")),
                    data.getString(data.getColumnIndex("name"))
            );
            Log.i(Config.LOG_TAG, "Got valid food data, food is " + result);
            listener.accept(result);
        } else {
            Log.w(Config.LOG_TAG, "cursor does not contain valid data");
            failureListener.accept(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(Config.LOG_TAG, "Loader reset");
    }
}
