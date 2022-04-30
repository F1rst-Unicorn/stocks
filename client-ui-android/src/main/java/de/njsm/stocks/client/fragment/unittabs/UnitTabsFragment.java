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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.presenter.UnitTabsViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class UnitTabsFragment extends BottomToolbarFragment {

    private UnitTabsViewModel unitTabsViewModel;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View content = insertContent(inflater, root, R.layout.fragment_tab_layout);
        SwipeRefreshLayout refreshLayout = content.findViewById(R.id.fragment_tab_layout_swipe);
        refreshLayout.setOnRefreshListener(() -> {
            onSwipeDown();
            refreshLayout.setRefreshing(false);
        });

        ViewPager2 viewPager = content.findViewById(R.id.fragment_tab_layout_pager);
        viewPager.setAdapter(new TabAdapter(this));
        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = content.findViewById(R.id.fragment_tab_layout_tabs);
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

        return root;
    }

    private void onSwipeDown() {
        unitTabsViewModel.synchronise();
    }

    @Override
    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        unitTabsViewModel = viewModelProvider.get(UnitTabsViewModel.class);
    }
}
