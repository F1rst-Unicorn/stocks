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
 */

package de.njsm.stocks.client.fragment;

import de.njsm.stocks.client.business.entities.LocationForDeletion;
import de.njsm.stocks.client.fragment.util.ListDiffer;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListDifferTest {

    private ListDiffer<LocationForDeletion> uut;

    @Before
    public void setup() {
        uut = byId(getOldList(), getNewList());
    }

    @Test
    public void passingNullIsAllowed() {
        uut = byId(null, null);

        assertThat(uut.getOldListSize(), is(0));
        assertThat(uut.getNewListSize(), is(0));
    }

    @Test
    public void gettingOldSizeWorks() {

        assertThat(uut.getOldListSize(), is(getOldList().size()));
    }

    @Test
    public void gettingNewSizeWorks() {

        assertThat(uut.getNewListSize(), is(getNewList().size()));
    }

    @Test
    public void equalItemIsReported() {
        assertThat(uut.areItemsTheSame(1, 0), is(true));
    }

    @Test
    public void distinctItemIsReported() {
        assertThat(uut.areItemsTheSame(0, 0), is(false));
    }

    @Test
    public void distinctItemContentIsReported() {
        assertThat(uut.areContentsTheSame(1, 0), is(false));
    }

    private List<LocationForDeletion> getOldList() {
        return Arrays.asList(
                LocationForDeletion.builder().id(2).version(0).build(),
                LocationForDeletion.builder().id(3).version(0).build()
        );
    }

    private List<LocationForDeletion> getNewList() {
        return Arrays.asList(
                LocationForDeletion.builder().id(3).version(1).build()
        );
    }
}
