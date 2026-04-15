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

package de.njsm.stocks.client.fragment.grocerystoreconflict;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.entities.GroceryStore;
import de.njsm.stocks.client.business.entities.GroceryStoreForEditing;
import de.njsm.stocks.client.business.entities.VersionedId;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.view.GroceryStoreForm;
import de.njsm.stocks.client.navigation.GroceryStoreConflictNavigator;
import de.njsm.stocks.client.presenter.GroceryStoreConflictViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class GroceryStoreConflictFragment extends BottomToolbarFragment implements MenuProvider {

    private GroceryStoreConflictViewModel groceryStoreConflictViewModel;

    private GroceryStoreConflictNavigator groceryStoreConflictNavigator;

    private GroceryStoreForm form;

    private VersionedId<GroceryStore> id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.form = new GroceryStoreForm(insertContent(inflater, root, R.layout.fragment_grocery_store_form), this::getString);

        long errorId = groceryStoreConflictNavigator.getErrorId(requireArguments());
        groceryStoreConflictViewModel.getGroceryStoreEditConflict(errorId).observe(getViewLifecycleOwner(), v -> {
            id = VersionedId.create(v.id(), v.remoteVersion());
            form.setName(v.name().suggestedValue());

            if (v.hasNoConflict()) {
                submit();
                return;
            }

            if (v.name().needsHandling()) {
                form.showNameConflict(v.name());
            } else {
                form.hideName();
            }
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
        submit();
        return true;
    }

    private void submit() {
        if (!form.maySubmit()) {
            form.setError(R.string.error_may_not_be_empty);
            return;
        }

        GroceryStoreForEditing data = GroceryStoreForEditing.builder()
                .id(id.id())
                .version(id.version())
                .name(form.getName())
                .build();
        groceryStoreConflictViewModel.edit(data);
        groceryStoreConflictNavigator.back();
    }

    @Inject
    @Override
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        groceryStoreConflictViewModel = viewModelProvider.get(GroceryStoreConflictViewModel.class);
    }

    @Inject
    void setGroceryStoreConflictNavigator(GroceryStoreConflictNavigator groceryStoreConflictNavigator) {
        this.groceryStoreConflictNavigator = groceryStoreConflictNavigator;
    }
}
