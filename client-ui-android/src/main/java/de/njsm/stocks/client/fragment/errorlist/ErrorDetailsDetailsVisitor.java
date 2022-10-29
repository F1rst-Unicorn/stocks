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

import javax.inject.Inject;

public class ErrorDetailsDetailsVisitor implements ErrorDetailsVisitor<Void, String> {

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    private final UnitRenderStrategy unitRenderStrategy;

    @Inject
    ErrorDetailsDetailsVisitor() {
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
}
