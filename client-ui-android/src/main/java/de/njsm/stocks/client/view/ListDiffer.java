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

package de.njsm.stocks.client.view;

import androidx.recyclerview.widget.DiffUtil;
import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Identifiable;

import java.util.Collections;
import java.util.List;

class ListDiffer<T extends Entity<T>> extends DiffUtil.Callback {

    static <T extends Entity<T>> ListDiffer<T> byId(List<? extends Identifiable<T>> oldList, List<? extends Identifiable<T>> newList) {
        return new ListDiffer<>(oldList, newList);
    }

    private final List<? extends Identifiable<T>> oldList;

    private final List<? extends Identifiable<T>> newList;

    public ListDiffer(List<? extends Identifiable<T>> oldList, List<? extends Identifiable<T>> newList) {
        this.oldList = oldList == null ? Collections.emptyList() : oldList;
        this.newList = newList == null ? Collections.emptyList() : newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Identifiable<T> oldItem = oldList.get(oldItemPosition);
        Identifiable<T> newItem = newList.get(newItemPosition);
        return oldItem.id() == newItem.id();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Identifiable<T> oldItem = oldList.get(oldItemPosition);
        Identifiable<T> newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
