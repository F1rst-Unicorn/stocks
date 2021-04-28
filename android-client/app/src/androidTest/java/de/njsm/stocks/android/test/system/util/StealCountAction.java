/* stocks is client-server program to manage a household's food stock
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

package de.njsm.stocks.android.test.system.util;


import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

public class StealCountAction implements ViewAction {

    private int count;

    @Override
    public Matcher<View> getConstraints() {
        return CoreMatchers.instanceOf(RecyclerView.class);
    }

    @Override
    public String getDescription() {
        return "Steal count action";
    }

    @Override
    public void perform(UiController uiController, View view) {
        count = ((RecyclerView) view).getAdapter().getItemCount();
    }

    public int getCount() {
        return count;
    }
}