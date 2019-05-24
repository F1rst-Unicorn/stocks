package de.njsm.stocks.util;

import android.view.View;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static androidx.test.espresso.intent.Checks.checkNotNull;

public class Matchers {

    public static Matcher<View> matchesDate(final int year, final int month, final int day) {
        return new BoundedMatcher<View, DatePicker>(DatePicker.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("matches date: %d-%d-%d",
                        year, month, day));
            }

            @Override
            protected boolean matchesSafely(DatePicker item) {
                int actualDay = item.getDayOfMonth();
                int actualMonth = item.getMonth();
                int actualYear = item.getYear();
                return (year == actualYear && month-1 == actualMonth && day == actualDay);
            }
        };
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
