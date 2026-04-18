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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.PriceForTableListing;
import de.njsm.stocks.client.business.entities.StoredAmount;
import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PriceShowViewModel extends ViewModel {

    private final ObservableListCache<PriceForTableListing> data;

    public PriceShowViewModel(ObservableListCache<PriceForTableListing> data) {
        this.data = data;
    }

    public LiveData<List<PriceForTableListing>> getPrices() {
        return data.getLiveData(() -> Observable.just(List.of(
                PriceForTableListing.create(
                        LocalDate.of(2025, 4, 17),
                        "BigShop City",
                        BigDecimal.valueOf(3.7),
                        StoredAmount.create(BigDecimal.valueOf(100), "g")
                ),
                PriceForTableListing.create(
                        LocalDate.of(2025, 4, 14),
                        "Farm Shop",
                        BigDecimal.valueOf(2.4),
                        StoredAmount.create(BigDecimal.valueOf(100), "g")
                ),
                PriceForTableListing.create(
                        LocalDate.of(2025, 4, 12),
                        "BuyAndEat Village",
                        BigDecimal.valueOf(4.1),
                        StoredAmount.create(BigDecimal.valueOf(100), "g")
                )
        )));
    }

    public void toggleShoppingFlag(Id<Food> foodId) {

    }
}
