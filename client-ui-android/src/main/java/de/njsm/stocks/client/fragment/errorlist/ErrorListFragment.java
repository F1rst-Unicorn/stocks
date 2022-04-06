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

package de.njsm.stocks.client.fragment.errorlist;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.navigation.ErrorListNavigator;
import de.njsm.stocks.client.presenter.ErrorListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.List;

public class ErrorListFragment extends BottomToolbarFragment {

    private TemplateSwipeList templateSwipeList;

    private ErrorListViewModel errorListViewModel;

    private ErrorListNavigator errorListNavigator;

    private ErrorDescriptionAdapter errorDescriptionAdapter;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);
        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        errorDescriptionAdapter = new ErrorDescriptionAdapter(this::onItemClicked);
        errorListViewModel.getErrors().observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_refresh_white_24dp),
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onListItemSwipedRight,
                this::onListItenSwipedLeft
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), errorDescriptionAdapter, callback);
        templateSwipeList.hideFloatingActionButton();
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        return root;
    }

    private void onListDataReceived(List<ErrorDescription> data) {
        if (data.isEmpty()) {
            templateSwipeList.setEmpty(R.string.text_no_errors);
        } else {
            templateSwipeList.setList();
        }
        errorDescriptionAdapter.setData(data);
    }

    private void onItemClicked(View listItem) {
        int listItemIndex = ((ErrorDescriptionViewHolder) listItem.getTag()).getBindingAdapterPosition();
        errorListViewModel.resolveId(listItemIndex, errorListNavigator::showErrorDetails);
    }

    private void onListItemSwipedRight(int listItemPosition) {
        errorListViewModel.retry(listItemPosition);
    }

    private void onListItenSwipedLeft(int listItemPosition) {
        errorListViewModel.delete(listItemPosition);
    }

    private void onSwipeDown() {
        errorListViewModel.synchronise();
    }

    @Inject
    public void setErrorListNavigator(ErrorListNavigator errorListNavigator) {
        this.errorListNavigator = errorListNavigator;
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        errorListViewModel = viewModelProvider.get(ErrorListViewModel.class);
    }
}
