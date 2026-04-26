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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.EntityDeleteRepository;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.PriceRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

class PriceRepositoryImpl implements PriceRepository, EntityDeleteRepository<Price> {

    private final PriceDao priceDao;

    private final Localiser localiser;

    @Inject
    PriceRepositoryImpl(PriceDao priceDao, Localiser localiser) {
        this.priceDao = priceDao;
        this.localiser = localiser;
    }

    @Override
    public PriceForDeletion getEntityForDeletion(Id<Price> id) {
        return priceDao.getPrice(id.id());
    }

    @Override
    public Observable<List<PriceForListing>> getPrices(IdImpl<Food> id) {
        return priceDao.getPricesOfFood(id.id())
                .map(v -> v.stream()
                        .map(item -> PriceForListing.create(
                                IdImpl.create(item.id),
                                localiser.toLocalDateTime(item.date),
                                item.groceryStoreName,
                                item.groceryChainName,
                                PricePerQuantity.create(
                                        item.price,
                                        StoredAmount.create(
                                                item.quantity,
                                                item.abbreviation

                                        ))
                        )).toList());
    }

    static class PriceListingData {
        int id;
        Instant date;
        String groceryStoreName;
        String groceryChainName;
        BigDecimal price;
        BigDecimal quantity;
        String abbreviation;
    }
}
