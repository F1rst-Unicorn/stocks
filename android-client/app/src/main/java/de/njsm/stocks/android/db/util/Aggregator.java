/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.db.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class Aggregator<I, O> implements Spliterator<O> {

    private final Iterator<I> iterator;

    private O current;

    public Aggregator(Iterator<I> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        while (iterator.hasNext()) {
            I r = iterator.next();
            if (current == null) {
                current = base(r);
            } else {
                if (sameGroup(current, r)) {
                    current = merge(current, r);
                } else {
                    action.accept(current);
                    current = base(r);
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

    public abstract O base(I input);

    public abstract boolean sameGroup(O current, I input);

    public abstract O merge(O current, I input);

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
