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

package de.njsm.stocks.client.business;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.List.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ListRegrouperTest {

    @Mock
    ListRegrouper.Callback<Integer, Integer> callback;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(callback);
    }

    @Test
    void singleWithoutInnerWorks() {
        List<Integer> outer = of(1);
        List<Integer> inner = of();

        test(outer, inner);

        verify(callback).outerFinished(1, of());
    }

    @Test
    void singleWithInnerWorks() {
        List<Integer> outer = of(1);
        List<Integer> inner = of(1);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1));
    }

    @Test
    void singleWithTwoInnerWorks() {
        List<Integer> outer = of(1);
        List<Integer> inner = of(1, 1);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1, 1));
    }

    @Test
    void twoOuterWithTwoInnerWorks() {
        List<Integer> outer = of(1, 2);
        List<Integer> inner = of(1, 2);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1));
        verify(callback).outerFinished(2, of(2));
    }

    @Test
    void twoOuterWithThreeInnerWorks() {
        List<Integer> outer = of(1, 2);
        List<Integer> inner = of(1, 1, 2);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1, 1));
        verify(callback).outerFinished(2, of(2));
    }

    @Test
    void middleMissingWorks() {
        List<Integer> outer = of(1, 2, 3);
        List<Integer> inner = of(1, 1, 3);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1, 1));
        verify(callback).outerFinished(2, of());
        verify(callback).outerFinished(3, of(3));
    }

    @Test
    void doubleFirstAndMiddleMissingWithTwoFollowingWorks() {
        List<Integer> outer = of(1, 2, 3, 4);
        List<Integer> inner = of(1, 1, 3, 4);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1, 1));
        verify(callback).outerFinished(2, of());
        verify(callback).outerFinished(3, of(3));
        verify(callback).outerFinished(4, of(4));
    }

    @Test
    void twoMiddleMissingWithFollowingWorks() {
        List<Integer> outer = of(1, 2, 3, 4);
        List<Integer> inner = of(1, 1, 4);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1, 1));
        verify(callback).outerFinished(2, of());
        verify(callback).outerFinished(3, of());
        verify(callback).outerFinished(4, of(4));
    }

    @Test
    void middleMissingWithTwoFollowingWorks() {
        List<Integer> outer = of(1, 2, 3, 4);
        List<Integer> inner = of(1, 3, 4);

        test(outer, inner);

        verify(callback).outerFinished(1, of(1));
        verify(callback).outerFinished(2, of());
        verify(callback).outerFinished(3, of(3));
        verify(callback).outerFinished(4, of(4));
    }

    private void test(List<Integer> outer, List<Integer> inner) {
        var uut = new ListRegrouper<>(
                new ListRegrouper.Group<>(outer.iterator(), t -> t),
                new ListRegrouper.Group<>(inner.iterator(), t -> t), callback);
        uut.execute();
    }
}