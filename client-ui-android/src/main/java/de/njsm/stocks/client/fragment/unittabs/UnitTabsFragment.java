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

package de.njsm.stocks.client.fragment.unittabs;

import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import de.njsm.stocks.client.fragment.scaledunitlist.ScaledUnitListFragment;
import de.njsm.stocks.client.fragment.unitlist.UnitListFragment;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class UnitTabsFragment extends TabsFragment {

    @Override
    public void setIcon(TabLayout.Tab tab, int position) {
        int iconId;
        if (position == 0) {
            iconId = R.drawable.ic_weight_numbered_black_24;
        } else {
            iconId = R.drawable.ic_weight_black_24;
        }
        tab.setIcon(iconId);
    }

    @NotNull
    @Override
    public List<Supplier<? extends Fragment>> fragmentFactories() {
        return List.of(
                ScaledUnitListFragment::new,
                UnitListFragment::new
        );
    }
}
