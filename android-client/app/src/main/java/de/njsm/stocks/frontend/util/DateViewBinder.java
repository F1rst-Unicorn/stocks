package de.njsm.stocks.frontend.util;

import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.njsm.stocks.backend.util.Config;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeParseException;

public class DateViewBinder implements SimpleCursorAdapter.ViewBinder{

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex("date")) {
            TextView text = (TextView) view;
            String dateString = cursor.getString(columnIndex);

            LocalDate date;
            try {
                date = LocalDate.from(Config.DATABASE_DATE_FORMAT.parse(dateString));
            } catch (DateTimeParseException e) {
                date = null;
            }
            assert date != null;

            text.setText(prettyPrint(date));
            return true;
        } else {
            return false;
        }
    }

    private CharSequence prettyPrint(LocalDate date) {
        LocalDate now = LocalDate.now();
        return DateUtils.getRelativeTimeSpanString(
                Instant.from(date.atStartOfDay(ZoneId.of("UTC"))).toEpochMilli(),
                Instant.from(now.atStartOfDay(ZoneId.of("UTC"))).toEpochMilli(),
                0L, DateUtils.FORMAT_ABBREV_ALL);
    }
}
