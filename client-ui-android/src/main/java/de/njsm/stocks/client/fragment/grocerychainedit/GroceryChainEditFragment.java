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

package de.njsm.stocks.client.fragment.grocerychainedit;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.GroceryChain;
import de.njsm.stocks.client.business.entities.GroceryChainForEditing;
import de.njsm.stocks.client.business.entities.VersionedId;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.GroceryChainForm;
import de.njsm.stocks.client.navigation.GroceryChainEditNavigator;
import de.njsm.stocks.client.presenter.GroceryChainEditViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class GroceryChainEditFragment extends BottomToolbarFragment implements MenuProvider {

    private GroceryChainEditViewModel groceryChainEditViewModel;

    private GroceryChainEditNavigator groceryChainEditNavigator;

    private GroceryChainForm form;

    private VersionedId<GroceryChain> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_grocery_chain_form);
        form = new GroceryChainForm(result, this::getString);
        id = groceryChainEditNavigator.getGroceryChainId(requireArguments());
        groceryChainEditViewModel.get(id).observe(getViewLifecycleOwner(), v -> form.setName(v.name()));

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

        GroceryChainForEditing data = GroceryChainForEditing.builder()
                .id(id.id())
                .version(id.version())
                .name(form.getName())
                .build();
        groceryChainEditViewModel.edit(data);
        groceryChainEditNavigator.back();
        return true;
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        groceryChainEditViewModel = viewModelProvider.get(GroceryChainEditViewModel.class);
    }

    @Inject
    void setNavigator(GroceryChainEditNavigator groceryChainEditNavigator) {
        this.groceryChainEditNavigator = groceryChainEditNavigator;
    }
}
