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

package de.njsm.stocks.client.fragment.fooditemadd;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodItemAddData;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.PriceAddForm;
import de.njsm.stocks.client.fragment.BottomToolbarFragment;
import de.njsm.stocks.client.fragment.DialogDisplayer;
import de.njsm.stocks.client.fragment.priceadd.PriceForm;
import de.njsm.stocks.client.fragment.view.FoodItemForm;
import de.njsm.stocks.client.navigation.FoodItemAddNavigator;
import de.njsm.stocks.client.presenter.FoodItemAddViewModel;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;

public class FoodItemAddFragment extends BottomToolbarFragment implements MenuProvider {

    private FoodItemAddViewModel foodItemAddViewModel;

    private FoodItemAddNavigator navigator;

    private FoodItemForm form;

    private PriceForm priceAddForm;

    private Id<Food> food;

    private Localiser clock;

    private DialogDisplayer dialogDisplayer;

    private Localiser localiser;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        View result = insertContent(inflater, root, R.layout.fragment_food_item_form);
        form = new FoodItemForm(result);
        form.setToday(clock.today());

        priceAddForm = new PriceForm(result.findViewById(R.id.fragment_food_item_form_price), this::getString);
        foodItemAddViewModel.getUnits().observe(getViewLifecycleOwner(), priceAddForm::showUnits);
        foodItemAddViewModel.getGroceryStores().observe(getViewLifecycleOwner(), priceAddForm::showGroceryStores);

        food = navigator.getFood(requireArguments());
        foodItemAddViewModel.getFormData(food).observe(getViewLifecycleOwner(), this::showForm);

        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    private void showForm(FoodItemAddData foodItemAddData) {
        if (foodItemAddData.locations().isEmpty()) {
            dialogDisplayer.showInformation(R.string.error_add_location_first);
            navigator.back();
            return;
        }

        form.showLocations(foodItemAddData.locations());
        form.showUnits(foodItemAddData.scaledUnits());
        form.setPredictionDate(foodItemAddData.predictedEatBy());
        requireActivity().setTitle(String.format(getString(R.string.title_add_item),
                        foodItemAddData.food().name()));
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.check_and_continue, menu);
    }

    @Override
    public boolean onMenuItemSelected( MenuItem menuItem) {
        de.njsm.stocks.client.business.entities.FoodItemForm data = de.njsm.stocks.client.business.entities.FoodItemForm.create(
                form.eatBy(),
                food.id(),
                form.storedIn().id(),
                form.unit().id()
        );
        foodItemAddViewModel.add(data);

        if (priceAddForm.maySubmit()) {
            priceAddForm.getGroceryStore().ifPresent(groceryStore ->
                    priceAddForm.getUnit().ifPresent(scaledUnit -> {
                        PriceAddForm priceToAdd = PriceAddForm.create(
                                priceAddForm.getPrice(),
                                localiser.toInstant(priceAddForm.getDate().atTime(priceAddForm.getTime())),
                                priceAddForm.getScale(),
                                groceryStore.toId(),
                                food.toId(),
                                scaledUnit.toId()
                        );
                        foodItemAddViewModel.add(priceToAdd);
                    }));
        }

        if (menuItem.getItemId() == R.id.menu_check_and_continue_check)
            navigator.back();
        return true;
    }

    @Inject
    @Override
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        super.setViewModelFactory(viewModelFactory);
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        foodItemAddViewModel = viewModelProvider.get(FoodItemAddViewModel.class);
    }

    @Inject
    void setNavigator(FoodItemAddNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setClock(Localiser clock) {
        this.clock = clock;
    }

    @Inject
    void setDialogDisplayer(DialogDisplayer dialogDisplayer) {
        this.dialogDisplayer = dialogDisplayer;
    }

    @Inject
    void setLocaliser(Localiser localiser) {
        this.localiser = localiser;
    }
}
