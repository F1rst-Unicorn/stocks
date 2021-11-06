/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.test.system;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import de.njsm.stocks.android.frontend.main.MainActivity;

public class SystemTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        IdlingResource resource = mActivityRule.getActivity().getResource().getNestedResource();
        IdlingRegistry.getInstance().register(resource);

        IdlingThreadPoolExecutor executor = (IdlingThreadPoolExecutor) mActivityRule.getActivity().getExecutor();
        IdlingRegistry.getInstance().register(executor);
    }

    @After
    public void tearDown() throws Exception {
        IdlingResource resource = mActivityRule.getActivity().getResource().getNestedResource();
        IdlingRegistry.getInstance().unregister(resource);
        IdlingThreadPoolExecutor executor = (IdlingThreadPoolExecutor) mActivityRule.getActivity().getExecutor();
        IdlingRegistry.getInstance().unregister(executor);
        mActivityRule.finishActivity();
    }
}
