package de.njsm.stocks.android.db.views;

import com.github.mikephil.charting.data.BarEntry;

public class BarEntryView {

    private float x;

    private float y;

    public BarEntryView(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public BarEntry map() {
        return new BarEntry(x, y);
    }
}
