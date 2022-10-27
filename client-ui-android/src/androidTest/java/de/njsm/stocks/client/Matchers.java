/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client;

import android.content.res.Resources;
import android.view.View;
import android.widget.DatePicker;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Id;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

import java.time.LocalDate;

public class Matchers {

    public static Matcher<View> matchesDate(LocalDate date) {
        return new BoundedMatcher<View, DatePicker>(DatePicker.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("matches date: %d-%d-%d",
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
            }

            @Override
            protected boolean matchesSafely(DatePicker item) {
                int actualDay = item.getDayOfMonth();
                int actualMonth = item.getMonth();
                int actualYear = item.getYear();
                return date.getYear() == actualYear
                        && date.getMonthValue() - 1 == actualMonth
                        && date.getDayOfMonth() == actualDay;
            }
        };
    }


    public static RecyclerViewMatcher recyclerView(int id) {
        return new RecyclerViewMatcher(id);
    }

    public static final class RecyclerViewMatcher {

        private final int recyclerViewId;

        private RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException e) {
                            idDescription = String.format("%s (resource name not found)", recyclerViewId);
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {
                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                        } else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }
                }
            };
        }
    }

    public static <T extends Entity<T>> Id<T> equalBy(Id<T> id) {
        return ArgumentMatchers.argThat(eqBy(id));
    }

    private static <T extends Entity<T>> ArgumentMatcher<Id<T>> eqBy(Id<T> id) {
        return actual -> actual.id() == id.id();
    }
}
