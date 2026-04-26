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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

class PriceShowInteractorImpl implements PriceShowInteractor {

    private final PriceShowRepository priceShowRepository;

    private final Localiser localiser;

    @Inject
    PriceShowInteractorImpl(PriceShowRepository priceShowRepository, Localiser localiser) {
        this.priceShowRepository = priceShowRepository;
        this.localiser = localiser;
    }

    @Override
    public Observable<PriceDetails> getPriceDetails(IdImpl<Food> id) {
        return Observable.combineLatest(
                        priceShowRepository.getPricesForTable(id),
                        priceShowRepository.getPricesForTable(id),
                (tableData, tsra) -> {
                    return PriceDetails.create(
                            tableData.stream()
                                    .map(this::mapPriceForTable)
                                    .toList(),
                            null /* TODO */,
                            null /* TODO */
                    );
                });
    }

    private PriceForTableListing mapPriceForTable(PriceForTableListingData v) {
        return PriceForTableListing.create(
                localiser.toLocalDateTime(v.date()),
                v.groceryStoreName() + " " + v.groceryChainName(),
                PricePerQuantity.create(
                        v.price(),
                        v.scale(),
                        StoredAmount.create(v.scaledUnitScale(), v.abbreviation())
                )
        );
    }
}
