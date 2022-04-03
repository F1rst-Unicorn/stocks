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

package de.njsm.stocks.client.fragment.listswipe;

import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class SwipeCallbackTest {

    private SwipeCallback uut;

    private Consumer<Integer> callback;

    private Consumer<Integer> leftCallback;

    private RecyclerView.ViewHolder viewHolder;

    @Before
    public void setup() {
        callback = mock(Consumer.class);
        leftCallback = mock(Consumer.class);
        viewHolder = mock(RecyclerView.ViewHolder.class);
        uut = new SwipeCallback(null, null, null, callback, leftCallback);
    }

    @Test
    public void rightSwipeIsDispatched() {
        int value = 42;
        Mockito.when(viewHolder.getBindingAdapterPosition()).thenReturn(value);

        uut.onSwiped(viewHolder, ItemTouchHelper.END);

        Mockito.verify(callback).accept(value);
    }

    @Test
    public void leftSwipeIsDispatched() {
        int value = 42;
        Mockito.when(viewHolder.getBindingAdapterPosition()).thenReturn(value);

        uut.onSwiped(viewHolder, ItemTouchHelper.START);

        Mockito.verify(leftCallback).accept(value);
    }

    @Test
    public void movingItemsIsNotSupported() {
        assertFalse(uut.onMove(null, null, null));
    }
}
