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

package de.njsm.stocks.android.frontend.fooditem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {

    private final FoodItemFragmentArgs input;

    private final FoodItemFragment parentFragment;

    public TabAdapter(@NonNull FoodItemFragment fragment, FoodItemFragmentArgs input) {
        super(fragment);
        this.parentFragment = fragment;
        this.input = input;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new FoodItemListFragment();
        } else {
            FoodDescriptionFragment fragmentSubtype = new FoodDescriptionFragment();
            fragmentSubtype.setSwiper(parentFragment);
            fragment = fragmentSubtype;
        }
        fragment.setArguments(input.toBundle());
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
