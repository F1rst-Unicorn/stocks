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

import de.njsm.stocks.client.business.entities.ErrorDetailsVisitor;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.LocationDeleteErrorDetails;
import de.njsm.stocks.client.business.entities.SynchronisationErrorDetails;
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
}
