package de.njsm.stocks.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.njsm.stocks.R;

public class FoodItemCursorAdapter extends SimpleCursorAdapter {

    protected int iconId;

    public FoodItemCursorAdapter(Context context,
                                 int layout,
                                 Cursor c,
                                 String[] from,
                                 int[] to,
                                 int flags,
                                 int resId) {
        super(context, layout, c, from, to, flags);

        iconId = resId;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        String dateString = cursor.getString(cursor.getColumnIndex("date"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

        Date date;
        Date now = new Date();
        Date inFiveDays = new Date(now.getTime() + 1000*60*60*24*5);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            date = null;
        }
        assert date != null;

        ImageView icon = (ImageView) view.findViewById(iconId);
        Drawable d;
        int red = context.getResources().getColor(android.R.color.holo_red_light);
        int green = context.getResources().getColor(R.color.colorPrimary);

        if (date.before(now)) {
            d = context.getResources().getDrawable(R.drawable.ic_error_black_24dp);
            assert d != null;
            d.setColorFilter(new PorterDuffColorFilter(
                    red,
                    PorterDuff.Mode.SRC_ATOP));

        } else if (date.after(inFiveDays)) {
            d = context.getResources().getDrawable(R.drawable.ic_check_black_24dp);
            assert d != null;
            d.setColorFilter(new PorterDuffColorFilter(
                    green,
                    PorterDuff.Mode.SRC_ATOP));
        } else {
            d = context.getResources().getDrawable(R.drawable.ic_alarm_black_24dp);
            assert d != null;
            d.setColorFilter(new PorterDuffColorFilter(
                    red,
                    PorterDuff.Mode.SRC_ATOP));
            icon.setImageDrawable(d);

        }

        icon.setImageDrawable(d);
    }
}
