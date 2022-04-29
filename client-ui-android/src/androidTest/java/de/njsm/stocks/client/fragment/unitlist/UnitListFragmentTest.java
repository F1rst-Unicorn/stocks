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

package de.njsm.stocks.client.fragment.unitlist;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeUnitListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.UnitDeleter;
import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.navigation.UnitListNavigator;
import de.njsm.stocks.client.testdata.UnitsForListing;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class UnitListFragmentTest {

    private FragmentScenario<UnitListFragment> scenario;

    private FakeUnitListInteractor unitListInteractor;

    private UnitListNavigator mockUnitListNavigator;

    private Synchroniser synchroniser;

    private UnitDeleter unitDeleter;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(UnitListFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @After
    public void tearDown() {
        reset(unitDeleter);
        reset(synchroniser);
        reset(mockUnitListNavigator);
    }

    @Test
    public void unitsAreListed() {
        unitListInteractor.setData(UnitsForListing.generate());

        for (UnitForListing item : UnitsForListing.generate()) {
            onView(withId(R.id.template_swipe_list_list))
                    .check(matches(withChild(withChild(allOf(withText(item.name()))))));
            onView(withId(R.id.template_swipe_list_list))
                    .check(matches(withChild(withChild(allOf(withText(item.abbreviation()))))));
        }
    }

    @Test
    public void emptyListShowsText() {
        unitListInteractor.setData(Collections.emptyList());

        onView(withId(R.id.template_swipe_list_empty_text))
                .check(matches(allOf(withEffectiveVisibility(Visibility.VISIBLE), withText(R.string.hint_no_units))));
    }

    @Test
    public void clickingAUnitNavigates() {
        int itemIndex = 1;
        List<UnitForListing> data = UnitsForListing.generate();
        assertTrue("The test wants to access element " + itemIndex, data.size() >= itemIndex + 1);
        UnitForListing Unit = data.get(itemIndex);
        assertTrue("Make sure the list position is mapped to an ID by having different values", Unit.id() != itemIndex);
        unitListInteractor.setData(data);

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, click()));

        verify(mockUnitListNavigator).editUnit(Unit.id());
    }

    @Test
    public void unitDeletionWorks() {
        List<UnitForListing> data = UnitsForListing.generate();
        assertFalse(data.isEmpty());
        unitListInteractor.setData(data);
        int itemIndex = 0;

        onView(withId(R.id.template_swipe_list_list))
                .perform(actionOnItemAtPosition(itemIndex, swipeRight()));

        verify(unitDeleter).deleteUnit(data.get(itemIndex));
    }

    @Test
    public void UnitAddingNavigates() {
        onView(withId(R.id.template_swipe_list_fab))
                .perform(click());

        verify(mockUnitListNavigator).addUnit();
    }

    @Inject
    public void setUnitListInteractor(FakeUnitListInteractor UnitListInteractor) {
        this.unitListInteractor = UnitListInteractor;
    }

    @Inject
    public void setSynchroniser(Synchroniser synchroniser) {
        this.synchroniser = synchroniser;
    }

    @Inject
    public void setMockUnitListNavigator(UnitListNavigator mockUnitListNavigator) {
        this.mockUnitListNavigator = mockUnitListNavigator;
    }

    @Inject
    public void setUnitDeleter(UnitDeleter UnitDeleter) {
        this.unitDeleter = UnitDeleter;
    }
}
