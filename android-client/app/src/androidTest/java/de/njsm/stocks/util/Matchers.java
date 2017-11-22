package de.njsm.stocks.util;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.DatePicker;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

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

}
