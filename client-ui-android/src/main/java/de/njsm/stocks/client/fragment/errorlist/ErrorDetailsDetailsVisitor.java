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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.presenter.UnitRenderStrategy;
import de.njsm.stocks.client.ui.R;

import javax.inject.Inject;
import java.util.function.Function;

public class ErrorDetailsDetailsVisitor implements ErrorDetailsVisitor<Void, String> {

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    private final UnitRenderStrategy unitRenderStrategy;

    private final Function<Integer, String> dictionary;

    @Inject
    ErrorDetailsDetailsVisitor(Function<Integer, String> dictionary) {
        this.dictionary = dictionary;
        this.unitAmountRenderStrategy = new UnitAmountRenderStrategy();
        this.unitRenderStrategy = new UnitRenderStrategy();
    }

    @Override
    public String locationAddForm(LocationAddForm locationAddForm, Void input) {
        return String.format("%1$s\n%2$s", locationAddForm.name(), locationAddForm.description());
    }

    @Override
    public String synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, Void input) {
        return "";
    }

    @Override
    public String locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, Void input) {
        return locationDeleteErrorDetails.name();
    }

    @Override
    public String locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, Void input) {
        return String.format("%1$s\n%2$s", locationEditErrorDetails.name(), locationEditErrorDetails.description());
    }

    @Override
    public String unitAddForm(UnitAddForm unitAddForm, Void input) {
        return unitRenderStrategy.render(unitAddForm);
    }

    @Override
    public String unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, Void input) {
        return unitRenderStrategy.render(unitDeleteErrorDetails);
    }

    @Override
    public String unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, Void input) {
        return unitRenderStrategy.render(unitEditErrorDetails);
    }

    @Override
    public String scaledUnitAddErrorDetails(ScaledUnitAddErrorDetails scaledUnitAddErrorDetails, Void input) {
        return unitAmountRenderStrategy.render(scaledUnitAddErrorDetails);
    }

    @Override
    public String scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, Void input) {
        return unitAmountRenderStrategy.render(scaledUnitEditErrorDetails);
    }

    @Override
    public String scaledUnitDeleteErrorDetails(ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails, Void input) {
        return unitAmountRenderStrategy.render(scaledUnitDeleteErrorDetails);
    }

    @Override
    public String foodAddErrorDetails(FoodAddErrorDetails foodAddErrorDetails, Void input) {
        return foodAddErrorDetails.name();
    }

    @Override
    public String foodDeleteErrorDetails(FoodDeleteErrorDetails foodDeleteErrorDetails, Void input) {
        return foodDeleteErrorDetails.name();
    }

    @Override
    public String foodEditErrorDetails(FoodEditErrorDetails foodEditErrorDetails, Void input) {
        return foodEditErrorDetails.name();
    }

    @Override
    public String foodItemAddErrorDetails(FoodItemAddErrorDetails foodItem, Void input) {
        return unitAmountRenderStrategy.render(foodItem.unit()) + " " + foodItem.foodName();
    }

    @Override
    public String foodItemDeleteErrorDetails(FoodItemDeleteErrorDetails foodItem, Void input) {
        return unitAmountRenderStrategy.render(foodItem.unit()) + " " + foodItem.foodName();
    }

    @Override
    public String foodItemEditErrorDetails(FoodItemEditErrorDetails foodItemEditErrorDetails, Void input) {
        return foodItemEditErrorDetails.foodName();
    }

    @Override
    public String eanNumberAddErrorDetails(EanNumberAddErrorDetails eanNumberAddErrorDetails, Void input) {
        return String.format("%s (%s)", eanNumberAddErrorDetails.foodName(), eanNumberAddErrorDetails.eanNumber());
    }

    @Override
    public String eanNumberDeleteErrorDetails(EanNumberDeleteErrorDetails eanNumberDeleteErrorDetails, Void input) {
        return String.format("%s (%s)", eanNumberDeleteErrorDetails.foodName(), eanNumberDeleteErrorDetails.eanNumber());
    }

    @Override
    public String userDeviceDeleteErrorDetails(UserDeviceDeleteErrorDetails userDeviceDeleteErrorDetails, Void input) {
        return String.format(dictionary.apply(R.string.error_details_user_device_format), userDeviceDeleteErrorDetails.userName(), userDeviceDeleteErrorDetails.deviceName());
    }

    @Override
    public String userDeleteErrorDetails(UserDeleteErrorDetails userDeleteErrorDetails, Void input) {
        return userDeleteErrorDetails.name();
    }

    @Override
    public String recipeAddErrorDetails(RecipeAddForm recipeAddForm, Void input) {
        return recipeAddForm.name();
    }

    @Override
    public String foodForBuying(FoodForBuying foodForBuying, Void input) {
        // not really used
        return String.valueOf(foodForBuying.id());
    }

    @Override
    public String userAddForm(UserAddForm userAddForm, Void input) {
        return userAddForm.name();
    }

    @Override
    public String userDeviceAddErrorDetails(UserDeviceAddErrorDetails userDeviceAddErrorDetails, Void input) {
        return String.format("%s (%s)", userDeviceAddErrorDetails.name(), userDeviceAddErrorDetails.ownerName());
    }
}
