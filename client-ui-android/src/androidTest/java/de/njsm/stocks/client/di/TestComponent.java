/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.client.di;

import android.content.Context;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeBusinessModule;
import de.njsm.stocks.client.execution.FakeExecutionModule;
import de.njsm.stocks.client.fragment.BottomToolbarFragmentTest;
import de.njsm.stocks.client.fragment.FragmentModule;
import de.njsm.stocks.client.fragment.SwipeDownSupportTest;
import de.njsm.stocks.client.fragment.allfood.AllFoodFragmentTest;
import de.njsm.stocks.client.fragment.emptyfood.EmptyFoodFragmentTest;
import de.njsm.stocks.client.fragment.errordetails.ErrorDetailsFragmentTest;
import de.njsm.stocks.client.fragment.errorlist.ErrorListFragmentTest;
import de.njsm.stocks.client.fragment.foodadd.FoodAddFragmentTest;
import de.njsm.stocks.client.fragment.foodconflict.FoodConflictFragmentTest;
import de.njsm.stocks.client.fragment.foodedit.FoodEditFragmentTest;
import de.njsm.stocks.client.fragment.foodinlocation.FoodInLocationFragmentTest;
import de.njsm.stocks.client.fragment.fooditemadd.FoodItemAddFragmentTest;
import de.njsm.stocks.client.fragment.fooditemconflict.FoodItemConflictFragmentTest;
import de.njsm.stocks.client.fragment.fooditemedit.FoodItemEditFragmentTest;
import de.njsm.stocks.client.fragment.fooditemlist.FoodItemListFragmentTest;
import de.njsm.stocks.client.fragment.locationadd.LocationAddFragmentTest;
import de.njsm.stocks.client.fragment.locationconflict.LocationConflictFragmentTest;
import de.njsm.stocks.client.fragment.locationedit.LocationEditFragmentTest;
import de.njsm.stocks.client.fragment.locationlist.LocationListFragmentTest;
import de.njsm.stocks.client.fragment.outline.OutlineFragmentTest;
import de.njsm.stocks.client.fragment.scaledunitadd.ScaledUnitAddFragmentTest;
import de.njsm.stocks.client.fragment.scaledunitconflict.ScaledUnitConflictFragmentTest;
import de.njsm.stocks.client.fragment.scaledunitedit.ScaledUnitEditFragmentTest;
import de.njsm.stocks.client.fragment.scaledunitlist.ScaledUnitListFragmentTest;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentTest;
import de.njsm.stocks.client.fragment.setupgreet.SetupGreetingFragmentTest;
import de.njsm.stocks.client.fragment.unitadd.UnitAddFragmentTest;
import de.njsm.stocks.client.fragment.unitconflict.UnitConflictFragmentTest;
import de.njsm.stocks.client.fragment.unitedit.UnitEditFragmentTest;
import de.njsm.stocks.client.fragment.unitlist.UnitListFragmentTest;
import de.njsm.stocks.client.fragment.userdevicelist.UserDeviceListFragmentTest;
import de.njsm.stocks.client.fragment.userlist.UserListFragmentTest;
import de.njsm.stocks.client.presenter.ViewModelModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        ViewModelModule.class,
        FragmentModule.class,
        FakeNavigationModule.class,
        TestActivityModule.class,
        FakeBusinessModule.class,
        FakeExecutionModule.class,
        FakePrimitiveModule.class,
})
public interface TestComponent {

    void inject(Application application);

    void inject(LocationListFragmentTest test);

    void inject(SetupGreetingFragmentTest test);

    void inject(SetupFormFragmentTest test);

    void inject(LocationAddFragmentTest test);

    void inject(BottomToolbarFragmentTest test);

    void inject(ErrorListFragmentTest test);

    void inject(SwipeDownSupportTest test);

    void inject(ErrorDetailsFragmentTest test);

    void inject(LocationEditFragmentTest test);

    void inject(LocationConflictFragmentTest test);

    void inject(UnitListFragmentTest test);

    void inject(ScaledUnitListFragmentTest test);

    void inject(UnitAddFragmentTest test);

    void inject(UnitEditFragmentTest test);

    void inject(UnitConflictFragmentTest test);

    void inject(ScaledUnitAddFragmentTest test);

    void inject(ScaledUnitEditFragmentTest test);

    void inject(ScaledUnitConflictFragmentTest test);

    void inject(OutlineFragmentTest test);

    void inject(FoodAddFragmentTest test);

    void inject(EmptyFoodFragmentTest test);

    void inject(FoodEditFragmentTest test);

    void inject(FoodConflictFragmentTest test);

    void inject(FoodInLocationFragmentTest test);

    void inject(AllFoodFragmentTest test);

    void inject(FoodItemListFragmentTest test);

    void inject(FoodItemAddFragmentTest test);

    void inject(FoodItemEditFragmentTest test);

    void inject(FoodItemConflictFragmentTest test);

    void inject(UserListFragmentTest test);

    void inject(UserDeviceListFragmentTest test);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application a);

        @BindsInstance
        Builder context(Context a);

        TestComponent build();
    }
}


