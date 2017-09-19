package de.njsm.stocks.frontend.util;

import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.njsm.stocks.backend.util.Config;

import java.text.ParseException;
import java.util.Date;

public class DateViewBinder implements SimpleCursorAdapter.ViewBinder{

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex("date")) {
            TextView text = (TextView) view;
            String dateString = cursor.getString(columnIndex);

            Date date;
            try {
                date = Config.DATABASE_DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                date = null;
            }
            assert date != null;

            text.setText(prettyPrint(date));
            return true;
        } else {
            return false;
        }
    }

    private CharSequence prettyPrint(Date date) {
        Date now = new Date();
        return DateUtils.getRelativeTimeSpanString(date.getTime(), now.getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL);
    }
}
