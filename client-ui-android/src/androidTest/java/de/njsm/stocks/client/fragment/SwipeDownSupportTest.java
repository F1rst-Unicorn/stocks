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

package de.njsm.stocks.client.fragment;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.fragment.emptyfood.EmptyFoodFragment;
import de.njsm.stocks.client.fragment.errorlist.ErrorListFragment;
import de.njsm.stocks.client.fragment.locationlist.LocationListFragment;
import de.njsm.stocks.client.fragment.outline.OutlineFragment;
import de.njsm.stocks.client.fragment.unittabs.UnitTabsFragment;
import de.njsm.stocks.client.ui.R;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.*;

public class SwipeDownSupportTest {

    private static final List<TestCaseData> TEST_CASES = Arrays.asList(
            new TestCaseData(LocationListFragment.class, R.id.template_swipe_list_swipe),
            new TestCaseData(ErrorListFragment.class, R.id.template_swipe_list_swipe),
            new TestCaseData(UnitTabsFragment.class, R.id.fragment_tab_layout_swipe),
            new TestCaseData(OutlineFragment.class, R.id.fragment_outline_swipe, 2),
            new TestCaseData(EmptyFoodFragment .class, R.id.template_swipe_list_swipe)
    );

    private Synchroniser synchroniser;

    @Test
    public void swipingDownSynchronises() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);

        for (TestCaseData testCase : TEST_CASES) {
            reset(synchroniser);
            FragmentScenario.launchInContainer(testCase.getFragment(), new Bundle(), R.style.StocksTheme);

            onView(withId(testCase.getSwiperId())).perform(swipeDown());

            verify(synchroniser, times(testCase.getInvocationTimes())).synchronise();
        }
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    private static final class TestCaseData {

        private final Class<? extends Fragment> fragment;

        @IdRes
        private final int swiperId;

        private final int invocationTimes;

        public TestCaseData(Class<? extends Fragment> fragment, int swiperId, int invocationTimes) {
            this.fragment = fragment;
            this.swiperId = swiperId;
            this.invocationTimes = invocationTimes;
        }

        public TestCaseData(Class<? extends Fragment> fragment, int swiperId) {
            this.fragment = fragment;
            this.swiperId = swiperId;
            this.invocationTimes = 1;
        }

        public Class<? extends Fragment> getFragment() {
            return fragment;
        }

        @IdRes
        public int getSwiperId() {
            return swiperId;
        }

        public int getInvocationTimes() {
            return invocationTimes;
        }
    }
}
