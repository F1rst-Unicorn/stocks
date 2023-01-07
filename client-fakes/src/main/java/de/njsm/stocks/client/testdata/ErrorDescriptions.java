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

package de.njsm.stocks.client.testdata;


import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErrorDescriptions {

    private final BehaviorSubject<List<ErrorDescription>> data;

    public ErrorDescriptions(List<ErrorDescription> data) {
        this.data = BehaviorSubject.createDefault(data);
    }

    public static List<ErrorDescription> generate() {
        return new ArrayList<>(Arrays.asList(
                ErrorDescription.create(1, StatusCode.DATABASE_UNREACHABLE, "", "", LocationAddForm.create("Fridge", "the cold one")),
                ErrorDescription.create(2, StatusCode.GENERAL_ERROR, "", "", SynchronisationErrorDetails.create()),
                ErrorDescription.create(3, StatusCode.INVALID_DATA_VERSION, "", "", LocationEditErrorDetails.create(3, "name", "description")),
                ErrorDescription.create(3, StatusCode.INVALID_DATA_VERSION, "", "", UnitEditErrorDetails.create(3, "name", "abbreviation"))
        ));
    }

    public BehaviorSubject<List<ErrorDescription>> getData() {
        return data;
    }
}
