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

package de.njsm.stocks.client.fragment.userdevicelist;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.User;
import de.njsm.stocks.client.business.entities.UserDevicesForListing;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.listswipe.SwipeCallback;
import de.njsm.stocks.client.fragment.view.TemplateSwipeList;
import de.njsm.stocks.client.fragment.view.TextWithPrefixIconViewHolder;
import de.njsm.stocks.client.navigation.UserDeviceListNavigator;
import de.njsm.stocks.client.presenter.UserDeviceListViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class UserDeviceListFragment extends BottomToolbarFragment implements MenuProvider {

    private UserDeviceListViewModel userDeviceListViewModel;

    private UserDeviceListNavigator userDeviceListNavigator;

    private UserDeviceAdapter userDeviceAdapter;

    private TemplateSwipeList templateSwipeList;

    private Id<User> userId;

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View swipeList = insertContent(inflater, root, R.layout.template_swipe_list);
        templateSwipeList = new TemplateSwipeList(swipeList);
        templateSwipeList.setLoading();

        userId = userDeviceListNavigator.getUserId(requireArguments());
        userDeviceAdapter = new UserDeviceAdapter(this::onListItemClicked, this::onListItemLongClicked);
        userDeviceListViewModel.get(userId).observe(getViewLifecycleOwner(), this::onListDataReceived);

        SwipeCallback callback = new SwipeCallback(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_white_24dp),
                new ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.colorAccent)),
                this::onItemSwipedRight
        );

        templateSwipeList.initialiseListWithSwiper(requireContext(), userDeviceAdapter, callback);
        templateSwipeList.bindFloatingActionButton(this::onAddItem);
        templateSwipeList.bindSwipeDown(this::onSwipeDown);

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

        return root;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.history, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_history) {
            userDeviceListNavigator.showHistory(userId);
            return true;
        }

        return false;
    }

    private boolean onListItemLongClicked(View listItem) {
        int listItemIndex = ((TextWithPrefixIconViewHolder) listItem.getTag()).getBindingAdapterPosition();
        userDeviceListViewModel.resolveId(listItemIndex, userDeviceListNavigator::showDeviceHistory);
        return true;
    }

    private void onListItemClicked(View listItem) {
        int listItemIndex = ((TextWithPrefixIconViewHolder) listItem.getTag()).getBindingAdapterPosition();
        userDeviceListViewModel.resolveId(listItemIndex, userDeviceListNavigator::showTicket);
    }

    private void onListDataReceived(UserDevicesForListing data) {
        templateSwipeList.setList();
        userDeviceAdapter.setData(data.devices());
        requireActivity().setTitle(data.userName());
    }

    private void onItemSwipedRight(int listItemIndex) {
        userDeviceListViewModel.delete(listItemIndex);
    }

    private void onAddItem(View button) {
        userDeviceListNavigator.add(userId);
    }

    public void onSwipeDown() {
        userDeviceListViewModel.synchronise();
    }

    @Inject
    public void setUserDeviceListNavigator(UserDeviceListNavigator userDeviceListNavigator) {
        this.userDeviceListNavigator = userDeviceListNavigator;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        userDeviceListViewModel = viewModelProvider.get(UserDeviceListViewModel.class);
    }
}
