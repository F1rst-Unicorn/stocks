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

package de.njsm.stocks.client.database;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class Aggregator<I, O> implements Spliterator<O> {

    private final Iterator<I> iterator;

    private final Function<I, O> base;

    private final BiPredicate<O, I> sameGroup;

    private final BiFunction<O, I, O> merge;

    private O current;

    public Aggregator(Iterator<I> iterator,
                      Function<I, O> base,
                      BiPredicate<O, I> sameGroup,
                      BiFunction<O, I, O> merge) {
        this.iterator = iterator;
        this.base = base;
        this.sameGroup = sameGroup;
        this.merge = merge;
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        while (iterator.hasNext()) {
            I r = iterator.next();
            if (current == null) {
                current = base.apply(r);
            } else {
                if (sameGroup.test(current, r)) {
                    current = merge.apply(current, r);
                } else {
                    action.accept(current);
                    current = base.apply(r);
                    return true;
                }
            }
        }

        if (current != null) {
            action.accept(current);
            current = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public java.util.Spliterator<O> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return iterator.hasNext() ? Long.MAX_VALUE : 0;
    }

    @Override
    public int characteristics() {
        return ORDERED | NONNULL | IMMUTABLE;
    }
}
