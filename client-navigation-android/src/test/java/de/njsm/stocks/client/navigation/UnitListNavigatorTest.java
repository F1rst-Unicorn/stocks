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

package de.njsm.stocks.client.navigation;

import androidx.navigation.ActionOnlyNavDirections;
import de.njsm.stocks.client.fragment.unittabs.UnitTabsFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UnitListNavigatorTest extends NavigationTest {

    private UnitListNavigator uut;

    @Before
    public void setUp() {
        uut = new UnitListNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void showingHistoryWorks() {

        uut.showUnitHistory();

        UnitTabsFragmentDirections.ActionNavFragmentUnitTabsToNavFragmentHistory actual = navigationArgConsumer.getLastArgument(UnitTabsFragmentDirections.ActionNavFragmentUnitTabsToNavFragmentHistory.class);
        assertThat(actual.getActionId(), is(R.id.action_nav_fragment_unit_tabs_to_nav_fragment_history));
    }

    @Test
    public void editingUnitContentBindsCorrectly() {
        int expectedId = 42;

        uut.editUnit(expectedId);

        UnitTabsFragmentDirections.ActionNavFragmentUnitTabsToNavFragmentUnitEdit actual = navigationArgConsumer.getLastArgument(UnitTabsFragmentDirections.ActionNavFragmentUnitTabsToNavFragmentUnitEdit.class);
        assertThat(actual.getId(), is(expectedId));
    }

    @Test
    public void addingUnitBindsCorrectly() {
        uut.addUnit();

        ActionOnlyNavDirections actual = navigationArgConsumer.getLastArgument(ActionOnlyNavDirections.class);
        assertThat(actual.getActionId(), is(R.id.action_nav_fragment_unit_tabs_to_nav_fragment_unit_add));
    }
}
