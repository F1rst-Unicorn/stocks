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

package de.njsm.stocks.client.fragment.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.databind.event.EventAdapter;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.HistoryNavigator;
import de.njsm.stocks.client.presenter.HistoryViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class HistoryFragment extends BottomToolbarFragment {

    private HistoryViewModel historyViewModel;

    private HistoryNavigator navigator;

    private Localiser localiser;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View listRoot = insertContent(inflater, root, R.layout.template_swipe_list);

        TemplateSwipeList templateSwipeList = new TemplateSwipeList(listRoot);
        templateSwipeList.bindSwipeDown(historyViewModel::synchronise);
        templateSwipeList.hideFloatingActionButton();

        EventAdapter adapter = new EventAdapter(
                this::doNothing,
                localiser,
                this::getString);
        templateSwipeList.initialiseList(requireContext(), adapter);
        RecyclerView activityFeed = listRoot.findViewById(R.id.template_swipe_list_list);
        adapter.addLoadStateListener(__ -> {
            activityFeed.scrollToPosition(0);
            return null;
        });
        getActivityFeed().observe(getViewLifecycleOwner(), v -> {
            adapter.submitData(getLifecycle(), v);
            templateSwipeList.setList();
        });

        return root;
    }

    private LiveData<PagingData<ActivityEvent>> getActivityFeed() {
        var food = navigator.getFood(requireArguments());
        if (food.isPresent())
            return historyViewModel.getActivityFeedForFood(food.get());

        var location = navigator.getLocation(requireArguments());
        if (location.isPresent())
            return historyViewModel.getActivityFeedForLocation(location.get());

        var user = navigator.getUser(requireArguments());
        if (user.isPresent())
            return historyViewModel.getActivityFeedForUser(user.get());

        var userDevice = navigator.getUserDevice(requireArguments());
        if (userDevice.isPresent())
            return historyViewModel.getActivityFeedForUserDevice(userDevice.get());

        return historyViewModel.getActivityFeed();
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        historyViewModel = viewModelProvider.get(HistoryViewModel.class);
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }

    @Inject
    void setNavigator(HistoryNavigator navigator) {
        this.navigator = navigator;
    }
}
