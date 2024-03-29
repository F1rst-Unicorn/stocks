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

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataModule {

    @Provides
    @Singleton
    public LocationsForListing locationsForListing() {
        return new LocationsForListing(LocationsForListing.generate());
    }

    @Provides
    @Singleton
    public ErrorDescriptions errorDescriptions() {
        return new ErrorDescriptions(ErrorDescriptions.generate());
    }

    @Provides
    @Singleton
    public UnitsForListing unitsForListing() {
        return new UnitsForListing(UnitsForListing.generate());
    }

    @Provides
    @Singleton
    public ScaledUnitsForListing ScaledUnitsForListing() {
        return new ScaledUnitsForListing(ScaledUnitsForListing.generate());
    }

    @Provides
    @Singleton
    public FoodsForListing FoodsForListing() {
        return new FoodsForListing(FoodsForListing.getEmpty());
    }

    @Provides
    @Singleton
    public UsersForListing UsersForListing() {
        return new UsersForListing(UsersForListing.generate());
    }

    @Provides
    @Singleton
    public UserDevicesForListing UserDevicesForListing() {
        return new UserDevicesForListing(UserDevicesForListing.generate());
    }

    @Provides
    @Singleton
    public RecipeTestData RecipeTestData() {
        return new RecipeTestData(RecipeTestData.generate());
    }
}
