package de.njsm.stocks.util;


import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

public class StealCountAction implements ViewAction {

    private int count;

    @Override
    public Matcher<View> getConstraints() {
        return CoreMatchers.instanceOf(RecyclerView.class);
    }

    @Override
    public String getDescription() {
        return "Steal count action";
    }

    @Override
    public void perform(UiController uiController, View view) {
        count = ((RecyclerView) view).getAdapter().getItemCount();
    }

    public int getCount() {
        return count;
    }
}