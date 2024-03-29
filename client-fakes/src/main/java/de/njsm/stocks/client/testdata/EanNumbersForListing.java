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


import de.njsm.stocks.client.business.entities.EanNumberForListing;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EanNumbersForListing {

    private final BehaviorSubject<List<EanNumberForListing>> data;

    @Inject
    EanNumbersForListing(List<EanNumberForListing> data) {
        this.data = BehaviorSubject.createDefault(data);
    }

    public static List<EanNumberForListing> generate() {
        return new ArrayList<>(Arrays.asList(
                EanNumberForListing.create(1, "7459832107820"),
                EanNumberForListing.create(2, "8231098443241"),
                EanNumberForListing.create(4, "4780932159202")
        ));
    }

    public BehaviorSubject<List<EanNumberForListing>> getData() {
        return data;
    }
}
