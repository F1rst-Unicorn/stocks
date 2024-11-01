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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ListRegrouper<E, F> {

    private final Group<E> first;

    private final Group<F> second;

    private final Callback<E, F> callback;

    public ListRegrouper(Group<E> first, Group<F> second, Callback<E, F> callback) {
        this.first = first;
        this.second = second;
        this.callback = callback;
    }

    public void execute() {
        List<F> inners = new ArrayList<>();
        int currentKey = -1;
        int currentInnerKey = -1;

        while (first.iterator.hasNext()) {
            E current = first.iterator.next();
            assert currentKey <= first.index.apply(current);
            currentKey = first.index.apply(current);

            while (second.iterator.hasNext()) {
                F currentInner = second.iterator.next();
                assert currentInnerKey <= second.index.apply(currentInner);
                currentInnerKey = second.index.apply(currentInner);

                while (currentInnerKey > currentKey && first.iterator.hasNext()) {
                    callback.outerFinished(current, inners);
                    current = first.iterator.next();
                    assert currentKey <= first.index.apply(current);
                    currentKey = first.index.apply(current);
                    inners = new ArrayList<>();
                }

                if (currentInnerKey == currentKey) {
                    inners.add(currentInner);
                }
            }

            callback.outerFinished(current, inners);
            inners = new ArrayList<>();
        }
    }

    public interface Callback<E, F> {
        void outerFinished(E current, List<F> inners);
    }

    public static final class Group<E> {

        private final Iterator<E> iterator;

        private final Function<E, Integer> index;

        public Group(Iterator<E> iterator, Function<E, Integer> index) {
            this.iterator = iterator;
            this.index = index;
        }
    }
}
