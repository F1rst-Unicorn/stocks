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

package de.njsm.stocks.client.fragment.eanlist;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.EanNumberForListing;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.databind.ScanEanNumberContract;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.util.CameraPermissionProber;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.EanNumberListNavigator;
import de.njsm.stocks.client.presenter.EanNumberListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class EanNumberListFragment extends BottomToolbarFragment implements CameraPermissionProber {

    private EanNumberListViewModel eanNumberListViewModel;

    private EanNumberListNavigator eanNumberListNavigator;

    private EanNumberAdapter eanNumberAdapter;

    private TemplateSwipeList templateSwipeList;

    private final ActivityResultLauncher<Activity> eanNumberScanOperation;

    private Id<Food> food;

    public EanNumberListFragment() {
        this.eanNumberScanOperation = registerForActivityResult(new ScanEanNumberContract(),
                v -> v.ifPresent(s -> eanNumberListViewModel.add(food, s)));
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);
        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        food = eanNumberListNavigator.getFood(requireArguments());

        eanNumberAdapter = new EanNumberAdapter();
        eanNumberListViewModel.get(food).observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), eanNumberAdapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        return root;
    }

    private void onAddItem(View view) {
        eanNumberScanOperation.launch(requireActivity());
    }

    private void onListDataReceived(List<EanNumberForListing> data) {
        templateSwipeList.setList();
        eanNumberAdapter.setData(data);
    }

    private void onItemSwipedRight(int listItemIndex) {
        eanNumberListViewModel.delete(listItemIndex);
    }

    public void onSwipeDown() {
        eanNumberListViewModel.synchronise();
    }

    @Inject
    public void setEanNumberListNavigator(EanNumberListNavigator eanNumberListNavigator) {
        this.eanNumberListNavigator = eanNumberListNavigator;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        eanNumberListViewModel = viewModelProvider.get(EanNumberListViewModel.class);
    }
}
