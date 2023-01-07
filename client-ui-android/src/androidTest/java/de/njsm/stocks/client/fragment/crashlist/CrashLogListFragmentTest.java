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

package de.njsm.stocks.client.fragment.crashlist;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.CrashListInteractor;
import de.njsm.stocks.client.business.entities.CrashLog;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.client.Matchers.recyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.*;

public class CrashLogListFragmentTest {

    private FragmentScenario<CrashLogListFragment> scenario;

    private CrashListInteractor interactor;

    @Before
    public void setUp() {
        Intents.init();
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        when(interactor.get()).thenReturn(Observable.just(getInput()));
        scenario = FragmentScenario.launchInContainer(CrashLogListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(interactor);
        Intents.release();
    }

    @Test
    public void clickingFiresSendIntent() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1)).perform(click());

        Intents.intended(allOf(
                IntentMatchers.hasAction(Intent.ACTION_SEND),
                IntentMatchers.hasExtra(Intent.EXTRA_TEXT, getInput().get(1).renderedContent()),
                IntentMatchers.hasExtra(Intent.EXTRA_EMAIL, new String[]{"stocks" + "@" + "njsm.de"}),
                IntentMatchers.hasExtra(Intent.EXTRA_SUBJECT, "Stocks Exception Log"),
                IntentMatchers.hasType("text/plain")
        ));
    }

    @Test
    public void uiIsShown() {
        DateRenderStrategy renderer = new DateRenderStrategy(null);
        onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(0, R.id.item_crash_log_name))
                .check(matches(withText(getInput().get(0).exceptionName())));
        onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(0, R.id.item_crash_log_date))
                .check(matches(withText(renderer.render(getInput().get(0).timeOccurred()))));
        onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(1, R.id.item_crash_log_name))
                .check(matches(withText(getInput().get(1).exceptionName())));
        onView(recyclerView(R.id.template_swipe_list_list).atPositionOnView(1, R.id.item_crash_log_date))
                .check(matches(withText(renderer.render(getInput().get(1).timeOccurred()))));
    }

    @Test
    public void swipingRightDeletes() {
        onView(recyclerView(R.id.template_swipe_list_list).atPosition(1)).perform(swipeRight());

        verify(interactor).delete(getInput().get(1));
    }

    private List<CrashLog> getInput() {
        return List.of(
                CrashLog.create("crashlog_123.txt", LocalDateTime.MAX, RuntimeException.class.getName(), "stacktrace"),
                CrashLog.create("crashlog_123.txt", LocalDateTime.MIN, IOException.class.getName(), "stacktrace")
        );
    }

    @Inject
    void setInteractor(CrashListInteractor interactor) {
        this.interactor = interactor;
    }
}
