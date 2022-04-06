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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.fragment.errorlist.ErrorListFragment;
import de.njsm.stocks.client.fragment.locationlist.LocationListFragment;
import de.njsm.stocks.client.ui.R;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class SwipeDownSupportTest {

    private static final List<Class<? extends Fragment>> FRAGMENTS = Arrays.asList(
            LocationListFragment.class,
            ErrorListFragment.class
    );

    private Synchroniser synchroniser;

    @Test
    public void swipingDownSynchronises() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);

        for (Class<? extends Fragment> fragmentClass : FRAGMENTS) {
            reset(synchroniser);
            FragmentScenario.launchInContainer(fragmentClass, new Bundle(), R.style.StocksTheme);

            onView(withId(R.id.template_swipe_list_swipe)).perform(swipeDown());

            verify(synchroniser).synchronise();
        }
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }
}
