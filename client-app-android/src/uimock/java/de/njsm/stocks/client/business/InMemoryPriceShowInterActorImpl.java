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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InMemoryPriceShowInterActorImpl implements PriceShowInteractor {

    @Inject
    InMemoryPriceShowInterActorImpl() {
    }

    @Override
    public Observable<PriceDetails> getPriceDetails(IdImpl<Food> id) {
        return Observable.just(PriceDetails.create(List.of(
                        PriceForTableListing.create(
                                LocalDate.of(2025, 4, 17),
                                "BigShop City",
                                BigDecimal.valueOf(3.7),
                                StoredAmount.create(BigDecimal.valueOf(100), "g")
                        ),
                        PriceForTableListing.create(
                                LocalDate.of(2025, 4, 14),
                                "BigShop Country",
                                BigDecimal.valueOf(2.4),
                                StoredAmount.create(BigDecimal.valueOf(100), "g")
                        ),
                        PriceForTableListing.create(
                                LocalDate.of(2025, 4, 12),
                                "BigShop Village",
                                BigDecimal.valueOf(4.1),
                                StoredAmount.create(BigDecimal.valueOf(100), "g")
                        )
                ), List.of(PricePlot.<GroceryChain, LocalDateTime>create(IdImpl.create(2), "BigShop", List.of(
                        PlotPoint.create(LocalDateTime.of(2025, 4, 12, 13, 0), BigDecimal.valueOf(4.1)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 12, 14, 0), BigDecimal.valueOf(4.5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 12, 15, 0), BigDecimal.valueOf(4.2)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 14, 13, 0), BigDecimal.valueOf(4.5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 14, 14, 0), BigDecimal.valueOf(3.5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 14, 15, 0), BigDecimal.valueOf(4.1)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 17, 13, 0), BigDecimal.valueOf(5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 17, 14, 0), BigDecimal.valueOf(4.2)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 17, 15, 0), BigDecimal.valueOf(4))
                ))),
                List.of(PricePlot.<GroceryStore, LocalDateTime>create(IdImpl.create(3), "BigShop City", List.of(
                        PlotPoint.create(LocalDateTime.of(2025, 4, 12, 15, 0), BigDecimal.valueOf(4.2)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 14, 15, 0), BigDecimal.valueOf(4.1)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 17, 15, 0), BigDecimal.valueOf(4))
                )), PricePlot.<GroceryStore, LocalDateTime>create(IdImpl.create(3), "BigShop Country", List.of(
                        PlotPoint.create(LocalDateTime.of(2025, 4, 12, 14, 0), BigDecimal.valueOf(4.5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 14, 14, 0), BigDecimal.valueOf(3.5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 17, 14, 0), BigDecimal.valueOf(4.2))
                )), PricePlot.<GroceryStore, LocalDateTime>create(IdImpl.create(3), "BigShop Village", List.of(
                        PlotPoint.create(LocalDateTime.of(2025, 4, 12, 13, 0), BigDecimal.valueOf(4.1)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 14, 13, 0), BigDecimal.valueOf(4.5)),
                        PlotPoint.create(LocalDateTime.of(2025, 4, 17, 13, 0), BigDecimal.valueOf(5))
                )))));
    }
}
