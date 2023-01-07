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

import android.annotation.SuppressLint;
import androidx.recyclerview.widget.DiffUtil;
import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Id;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ItemDiffer<T> extends DiffUtil.ItemCallback<T> {

    public static <I extends Entity<I>, T extends Id<I>> ItemDiffer<T> byId() {
        return new ItemDiffer<>(v -> (long) v.id());
    }

    public static <T> ItemDiffer<T> byId(Function<T, Object> idMapper) {
        return new ItemDiffer<>(idMapper);
    }

    private final Function<T, Object> idMapper;

    private ItemDiffer(Function<T, Object> idMapper) {
        this.idMapper = idMapper;
    }

    @Override
    public boolean areItemsTheSame(@NotNull T oldItem, @NotNull T newItem) {
        return idMapper.apply(oldItem).equals(idMapper.apply(newItem));
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NotNull T oldItem, @NotNull T newItem) {
        return oldItem.equals(newItem);
    }
}
