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

package de.njsm.stocks.client.fragment.grocerystoreedit;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.GroceryStore;
import de.njsm.stocks.client.business.entities.GroceryStoreForEditing;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.VersionedId;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.GroceryStoreForm;
import de.njsm.stocks.client.navigation.GroceryStoreEditNavigator;
import de.njsm.stocks.client.presenter.GroceryStoreEditViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class GroceryStoreEditFragment extends BottomToolbarFragment implements MenuProvider {

    private GroceryStoreEditViewModel groceryStoreEditViewModel;

    private GroceryStoreEditNavigator groceryStoreEditNavigator;

    private GroceryStoreForm form;

    private VersionedId<GroceryStore> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_grocery_store_form);
        form = new GroceryStoreForm(result, this::getString);
        IdImpl<GroceryStore> id = groceryStoreEditNavigator.getGroceryStoreId(requireArguments());
        groceryStoreEditViewModel.get(id).observe(getViewLifecycleOwner(), v -> {
            this.id = v.toVersion();
            form.setName(v.name());
            form.setGroceryChainId(v.groceryChain());
            requireActivity().setTitle(String.format(getString(R.string.title_edit_grocery_store),
                    v.groceryChainName()));
        });

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (!form.maySubmit()) {
            form.setError(R.string.error_may_not_be_empty);
            return true;
        }

        GroceryStoreForEditing data = GroceryStoreForEditing.builder()
                .id(id.id())
                .version(id.version())
                .name(form.getName())
                .groceryChain(form.getGroceryChainId())
                .build();
        groceryStoreEditViewModel.edit(data);
        groceryStoreEditNavigator.back();
        return true;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        groceryStoreEditViewModel = viewModelProvider.get(GroceryStoreEditViewModel.class);
    }

    @Inject
    void setNavigator(GroceryStoreEditNavigator groceryStoreEditNavigator) {
        this.groceryStoreEditNavigator = groceryStoreEditNavigator;
    }
}
