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

package de.njsm.stocks.client.fragment.util;

import androidx.recyclerview.widget.DiffUtil;
import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ListDiffer<T> extends DiffUtil.Callback {

    public static <I extends Entity<I>, T extends Id<I>> ListDiffer<T> byId(List<T> oldList, List<T> newList) {
        return new ListDiffer<>(oldList, newList, v -> (long) v.id());
    }

    public static <T> ListDiffer<T> byId(List<T> oldList, List<T> newList, Function<T, Long> idMapper) {
        return new ListDiffer<>(oldList, newList, idMapper);
    }

    public static <T, I extends Entity<I>> ListDiffer<T> byNestedId(List<T> oldList, List<T> newList, Function<T, IdImpl<I>> idMapper) {
        return new ListDiffer<>(oldList, newList, idMapper.andThen(IdImpl::longId));
    }

    private final List<T> oldList;

    private final List<T> newList;

    private final Function<T, Long> idMapper;

    private ListDiffer(List<T> oldList, List<T> newList, Function<T, Long> idMapper) {
        this.oldList = oldList == null ? Collections.emptyList() : oldList;
        this.newList = newList == null ? Collections.emptyList() : newList;
        this.idMapper = idMapper;
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
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return idMapper.apply(oldItem).equals(idMapper.apply(newItem));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
