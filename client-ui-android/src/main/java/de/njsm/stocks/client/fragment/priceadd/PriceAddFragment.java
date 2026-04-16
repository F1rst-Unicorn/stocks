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

package de.njsm.stocks.client.fragment.priceadd;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.PriceAddForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.navigation.PriceAddNavigator;
import de.njsm.stocks.client.presenter.PriceAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class PriceAddFragment extends BottomToolbarFragment implements MenuProvider {

    private PriceAddViewModel viewModel;

    private PriceAddNavigator navigator;

    private PriceForm form;

    private Localiser localiser;

    private IdImpl<Food> foodId;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        foodId = navigator.getFoodId(requireArguments());

        View result = insertContent(inflater, root, R.layout.fragment_price_form_standalone);
        form = new PriceForm(result, this::getString);

        viewModel.getUnits().observe(getViewLifecycleOwner(), form::showUnits);
        viewModel.getGroceryStores().observe(getViewLifecycleOwner(), form::showGroceryStores);

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.check, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        if (!form.maySubmit()) {
            form.setError(R.string.error_may_not_be_empty);
            return true;
        }

        form.getGroceryStore().ifPresent(groceryStore ->
                form.getUnit().ifPresent(scaledUnit -> {
                        PriceAddForm data = PriceAddForm.create(
                                form.getPrice(),
                                localiser.toInstant(form.getDate().atTime(form.getTime())),
                                form.getScale(),
                                groceryStore.toId(),
                                foodId,
                                scaledUnit.toId()
                        );
                        viewModel.add(data);
                        navigator.back();
        }));
        return true;
    }

    @Inject
    @Override
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(PriceAddViewModel.class);
    }

    @Inject
    void setNavigator(PriceAddNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }
}
