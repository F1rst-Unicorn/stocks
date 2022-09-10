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

import de.njsm.stocks.client.fragment.errorlist.ErrorListFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class ErrorListNavigatorImplTest extends NavigationTest {

    private ErrorListNavigator uut;

    @Before
    public void setUp() {
        uut = new ErrorListNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void goingToErrorDetailsNavigates() {
        long input = 3;

        uut.showErrorDetails(input);

        ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentErrorDetail direction = navigationArgConsumer.getLastArgument(ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentErrorDetail.class);
        assertThat(direction.getId(), is(input));
    }

    @Test
    public void goingToLocationConflictResolutionNavigates() {
        long input = 3;

        uut.resolveLocationEditConflict(input);

        ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentLocationConflict direction = navigationArgConsumer.getLastArgument(ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentLocationConflict.class);
        assertThat(direction.getId(), is(input));
    }

    @Test
    public void goingToUnitConflictResolutionNavigates() {
        long input = 3;

        uut.resolveUnitEditConflict(input);

        ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentUnitConflict direction = navigationArgConsumer.getLastArgument(ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentUnitConflict.class);
        assertThat(direction.getId(), is(input));
    }

    @Test
    public void goingToScaledUnitConflictResolutionNavigates() {
        long input = 3;

        uut.resolveScaledUnitEditConflict(input);

        ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentScaledUnitConflict direction = navigationArgConsumer.getLastArgument(ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentScaledUnitConflict.class);
        assertThat(direction.getId(), is(input));
    }

    @Test
    public void goingToFoodConflictResolutionNavigates() {
        long input = 3;

        uut.resolveFoodEditConflict(input);

        ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentFoodConflict direction = navigationArgConsumer.getLastArgument(ErrorListFragmentDirections.ActionNavFragmentErrorListToNavFragmentFoodConflict.class);
        assertThat(direction.getId(), is(input));
    }
}
