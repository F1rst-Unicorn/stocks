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

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.databind.event.EventAdapter;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.OutlineNavigator;
import de.njsm.stocks.client.presenter.OutlineViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class OutlineFragment extends BottomToolbarFragment implements MenuProvider {

    private OutlineNavigator outlineNavigator;

    private OutlineViewModel outlineViewModel;

    private Localiser localiser;

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

        RecyclerView activityFeed = content.findViewById(R.id.fragment_outline_list);
        activityFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        EventAdapter adapter = new EventAdapter(
                this::onActivityFeedItemClicked,
                localiser,
                this::getString);
        activityFeed.setAdapter(adapter);
        outlineViewModel.getActivityFeed().observe(getViewLifecycleOwner(), v -> {
            activityFeed.scrollToPosition(0);
            adapter.submitData(getLifecycle(), v);
        });

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_outline_options, menu);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_outline_options_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(requireContext(), requireActivity().getClass())));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnPrimary));
        editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.hintColorOnPrimary));
    }

    private void onActivityFeedItemClicked(View view) {

    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
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

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }
}
