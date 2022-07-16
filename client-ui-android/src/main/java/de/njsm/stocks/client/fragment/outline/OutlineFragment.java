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

package de.njsm.stocks.client.fragment.outline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.OutlineNavigator;
import de.njsm.stocks.client.presenter.OutlineViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class OutlineFragment extends BottomToolbarFragment {

    private OutlineNavigator outlineNavigator;

    private OutlineViewModel outlineViewModel;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View content = insertContent(inflater, root, R.layout.fragment_outline);
        content.findViewById(R.id.fragment_outline_fab).setOnClickListener(this::onAddFood);
        content.findViewById(R.id.fragment_outline_cardview).setOnClickListener(this::onShowAllFood);
        content.findViewById(R.id.fragment_outline_cardview2).setOnClickListener(this::onShowEmptyFood);
        SwipeRefreshLayout refreshLayout = content.findViewById(R.id.fragment_outline_swipe);
        refreshLayout.setOnRefreshListener(() -> {
            outlineViewModel.synchronise();
            refreshLayout.setRefreshing(false);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        outlineViewModel.synchronise();
    }

    private void onShowEmptyFood(View view) {
        outlineNavigator.showEmptyFood();
    }

    private void onShowAllFood(View view) {
        outlineNavigator.showAllFood();
    }

    private void onAddFood(View view) {
        outlineNavigator.addFood();
    }

    @Inject
    void setOutlineNavigator(OutlineNavigator outlineNavigator) {
        this.outlineNavigator = outlineNavigator;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        outlineViewModel = viewModelProvider.get(OutlineViewModel.class);
    }
}
