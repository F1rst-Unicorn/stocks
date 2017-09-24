package de.njsm.stocks.util;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.AdapterView;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.instanceOf;

public class StealCountAction implements ViewAction {

    private int count;

    @Override
    public Matcher<View> getConstraints() {
        return instanceOf(AdapterView.class);
    }

    @Override
    public String getDescription() {
        return "Steal count action";
    }

    @Override
    public void perform(UiController uiController, View view) {
        count = ((AdapterView) view).getCount();
    }

    public int getCount() {
        return count;
    }
}