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

package de.njsm.stocks.android.frontend.units;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;

import javax.inject.Inject;

public class UnitPagingFragment extends BaseFragment {

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_unit_pager, container, false);

        ViewPager2 viewPager = result.findViewById(R.id.fragment_unit_pager_pager);
        viewPager.setAdapter(new TabAdapter(this));
        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = result.findViewById(R.id.fragment_unit_pager_tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    int iconId;
                    if (position == 0) {
                        iconId = R.drawable.ic_weight_numbered_black_24;
                    } else {
                        iconId = R.drawable.ic_weight_black_24;
                    }
                    tab.setIcon(iconId);
                }
        ).attach();

        initialiseSwipeRefresh(result, R.id.fragment_unit_pager_swipe, viewModelFactory);

        return result;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }
}
