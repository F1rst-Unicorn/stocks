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

package de.njsm.stocks.client.fragment.unithistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.databind.event.EventAdapter;
import de.njsm.stocks.client.fragment.InjectableFragment;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.presenter.UnitHistoryViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class UnitHistoryFragment extends InjectableFragment {

    private UnitHistoryViewModel unitHistoryViewModel;

    private Localiser localiser;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.template_swipe_list, container, false);
        TemplateSwipeList templateSwipeList = new TemplateSwipeList(root);

        EventAdapter adapter = new EventAdapter(
                this::doNothing,
                localiser,
                this::getString);
        templateSwipeList.initialiseList(requireContext(), adapter);
        RecyclerView activityFeed = root.findViewById(R.id.template_swipe_list_list);
        unitHistoryViewModel.getActivityFeed().observe(getViewLifecycleOwner(), v -> {
            activityFeed.scrollToPosition(0);
            adapter.submitData(getLifecycle(), v);
            templateSwipeList.setList();
        });

        return root;
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        unitHistoryViewModel = viewModelProvider.get(UnitHistoryViewModel.class);
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }
}
