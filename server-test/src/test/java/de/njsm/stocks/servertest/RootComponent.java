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

package de.njsm.stocks.servertest;

import dagger.Component;
import de.njsm.stocks.client.crypto.CryptoModule;
import de.njsm.stocks.client.network.NetworkModule;
import de.njsm.stocks.servertest.v2.*;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                NetworkModule.class,
                CryptoModule.class,
                TestModule.class,
        }
)
public interface RootComponent {

    void inject(LocationTest locationTest);

    void inject(FoodItemTest foodItemTest);

    void inject(UpdateChangeTest updateChangeTest);

    void inject(FoodTest foodTest);

    void inject(UnitTest unitTest);

    void inject(ScaledUnitTest scaledUnitTest);

    void inject(RecipeTest recipeTest);

    @Component.Builder
    interface Builder {
        RootComponent build();
    }
}
