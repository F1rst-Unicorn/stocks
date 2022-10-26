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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.presenter.TabsViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

public abstract class TabsFragment extends BottomToolbarFragment {

    private TabsViewModel tabsViewModel;

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
        viewPager.setAdapter(new TabAdapter(this, fragmentFactories()));
        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = content.findViewById(R.id.fragment_tab_layout_tabs);
        new TabLayoutMediator(tabLayout, viewPager, this::setIcon).attach();

        return root;
    }

    public abstract void setIcon(TabLayout.Tab tab, int position);

    public abstract List<Supplier<? extends Fragment>> fragmentFactories();

    private void onSwipeDown() {
        tabsViewModel.synchronise();
    }

    @Override
    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        tabsViewModel = viewModelProvider.get(TabsViewModel.class);
    }
}
