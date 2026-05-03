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
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

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
                        priceShowRepository.getPricePlotData(id),
                (tableData, plotPointData) -> {
                    var plotData = groupPricePlotDataByChainAndStore(plotPointData);
                    return PriceDetails.create(
                            tableData.stream()
                                    .map(this::mapPriceForTable)
                                    .toList(),
                            plotData.byChain,
                            plotData.byStore
                    );
                });
    }

    private PriceForTableListing mapPriceForTable(PriceForTableListingData v) {
        return PriceForTableListing.create(
                localiser.toLocalDateTime(v.date()),
                v.groceryChainName() + " " + v.groceryStoreName(),
                PricePerQuantity.create(
                        v.price(),
                        v.scale(),
                        StoredAmount.create(v.scaledUnitScale(), v.abbreviation())
                )
        );
    }

    private PlotPointData groupPricePlotDataByChainAndStore(List<PriceForPlotPointData> data) {
        Map<IdImpl<Unit>, Map<IdImpl<GroceryChain>, Map<IdImpl<GroceryStore>, List<PriceForPlotPointData>>>> pricesByUnitByStore = data.stream()
                .collect(groupingBy(PriceForPlotPointData::unitId,
                        groupingBy(PriceForPlotPointData::groceryChainId,
                                groupingBy(PriceForPlotPointData::groceryStoreId))));

        var plotPointByChain = pricesByUnitByStore.entrySet().stream()
                .flatMap(unit -> unit.getValue().entrySet().stream()
                    .map(groceryChain -> {
                            var allPrices = groceryChain.getValue().values()
                                    .stream()
                                    .flatMap(List::stream)
                                    .map(v -> PlotPoint.create(localiser.toLocalDateTime(v.date()), v.toPricePerQuantity().normalisedPrice()))
                                    .sorted(Comparator.comparing(PlotPoint::x))
                                    .toList();
                            PriceForPlotPointData first = groceryChain.getValue().values().stream().findAny().get().get(0);
                            return PricePlot.create(
                                    groceryChain.getKey(),
                                    first.groceryChainName() + " (" + first.abbreviation() + ")",
                                    allPrices);
                        }))
                .toList();

        var plotPointByStore = pricesByUnitByStore.entrySet().stream()
                .flatMap(unit -> unit.getValue().entrySet().stream()
                    .flatMap(v -> v.getValue().values().stream())
                    .map(pricesOfGroceryStore -> {
                        var first = pricesOfGroceryStore.get(0);
                        var prices = pricesOfGroceryStore.stream()
                                .map(v -> PlotPoint.create(localiser.toLocalDateTime(v.date()), v.toPricePerQuantity().normalisedPrice()))
                                .sorted(Comparator.comparing(PlotPoint::x))
                                .toList();
                        return PricePlot.create(first.groceryStoreId(), first.groceryChainName() + " " + first.groceryStoreName() + " (" + first.abbreviation() + ")", prices);
                    })
                )
                .toList();

        return new PlotPointData(plotPointByChain, plotPointByStore);
    }

    static class PlotPointData {
        List<PricePlot<GroceryChain, LocalDateTime>> byChain;
        List<PricePlot<GroceryStore, LocalDateTime>> byStore;

        public PlotPointData(List<PricePlot<GroceryChain, LocalDateTime>> byChain, List<PricePlot<GroceryStore, LocalDateTime>> byStore) {
            this.byChain = byChain;
            this.byStore = byStore;
        }
    }
}
