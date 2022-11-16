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

package de.njsm.stocks.client.databind;

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.ui.R;

public class ErrorDetailsHeadlineVisitor implements ErrorDetailsVisitor<Void, Integer> {

    @Override
    public Integer locationAddForm(LocationAddForm locationAddForm, Void input) {
        return R.string.error_details_location_add_error_list;
    }

    @Override
    public Integer synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, Void input) {
        return R.string.error_details_synchronisation_error_list;
    }

    @Override
    public Integer locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, Void input) {
        return R.string.error_details_location_delete_error_list;
    }

    @Override
    public Integer locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, Void input) {
        return R.string.error_details_location_edit_error_list;
    }

    @Override
    public Integer unitAddForm(UnitAddForm unitAddForm, Void input) {
        return R.string.error_details_unit_add_error_list;
    }

    @Override
    public Integer unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, Void input) {
        return R.string.error_details_unit_delete_error_list;
    }

    @Override
    public Integer unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, Void input) {
        return R.string.error_details_unit_edit_error_list;
    }

    @Override
    public Integer scaledUnitAddErrorDetails(ScaledUnitAddErrorDetails scaledUnitAddErrorDetails, Void input) {
        return R.string.error_details_scaled_unit_add_error_list;
    }

    @Override
    public Integer scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, Void input) {
        return R.string.error_details_scaled_unit_edit_error_list;
    }

    @Override
    public Integer scaledUnitDeleteErrorDetails(ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails, Void input) {
        return R.string.error_details_scaled_unit_delete_error_list;
    }

    @Override
    public Integer foodAddErrorDetails(FoodAddErrorDetails foodAddErrorDetails, Void input) {
        return R.string.error_details_food_add_error_list;
    }

    @Override
    public Integer foodDeleteErrorDetails(FoodDeleteErrorDetails foodDeleteErrorDetails, Void input) {
        return R.string.error_details_food_delete_error_list;
    }

    @Override
    public Integer foodEditErrorDetails(FoodEditErrorDetails foodEditErrorDetails, Void input) {
        return R.string.error_details_food_edit_error_list;
    }

    @Override
    public Integer foodItemAddErrorDetails(FoodItemAddErrorDetails foodItemAddErrorDetails, Void input) {
        return R.string.error_details_food_item_add_error_list;
    }

    @Override
    public Integer foodItemDeleteErrorDetails(FoodItemDeleteErrorDetails foodItemDeleteErrorDetails, Void input) {
        return R.string.error_details_food_item_delete_error_list;
    }

    @Override
    public Integer foodItemEditErrorDetails(FoodItemEditErrorDetails foodItemEditErrorDetails, Void input) {
        return R.string.error_details_food_item_edit_error_list;
    }

    @Override
    public Integer eanNumberAddErrorDetails(EanNumberAddErrorDetails eanNumberAddErrorDetails, Void input) {
        return R.string.error_details_ean_number_add_error_list;
    }

    @Override
    public Integer eanNumberDeleteErrorDetails(EanNumberDeleteErrorDetails eanNumberAddErrorDetails, Void input) {
        return R.string.error_details_ean_number_delete_error_list;
    }

    @Override
    public Integer userDeviceDeleteErrorDetails(UserDeviceDeleteErrorDetails userDeviceDeleteErrorDetails, Void input) {
        return R.string.error_details_user_device_delete_error_list;
    }
}
