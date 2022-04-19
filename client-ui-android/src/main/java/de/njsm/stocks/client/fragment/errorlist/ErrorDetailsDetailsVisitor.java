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

public class ErrorDetailsDetailsVisitor implements ErrorDetailsVisitor<Void, String> {

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
}
