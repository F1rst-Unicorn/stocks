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

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.event.ActivityEventPage;
import de.njsm.stocks.client.business.event.EventInteractor;
import de.njsm.stocks.client.business.event.EventInteractorFactory;
import de.njsm.stocks.client.business.event.UnitEventInteractor;
import de.njsm.stocks.client.execution.Scheduler;
import de.njsm.stocks.client.testdata.LocationsForSelection;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import de.njsm.stocks.client.testdata.UnitsForSelection;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Singleton;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module
public interface FakeBusinessModule {

    @Binds
    LocationListInteractor locationListInteractor(FakeLocationListInteractor locationListInteractor);

    @Provides
    @Singleton
    static FakeLocationListInteractor fakeLocationListInteractor() {
        return new FakeLocationListInteractor();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<Location> locationDeleter() {
        return mock(EntityDeleter.class);
    }

    @Provides
    @Singleton
    static Synchroniser synchroniser() {
        return mock(Synchroniser.class);
    }


    @Provides
    @Singleton
    static SetupStatusChecker setupStatusChecker() {
        return mock(SetupStatusChecker.class);
    }

    @Provides
    @Singleton
    static SetupRunner setupRunner() {
        return mock(SetupRunner.class);
    }

    @Provides
    @Singleton
    static Scheduler scheduler() {
        return mock(Scheduler.class);
    }

    @Provides
    @Singleton
    static LocationAddInteractor locationAddInteractor() {
        return mock(LocationAddInteractor.class);
    }

    @Provides
    @Singleton
    static ErrorRetryInteractor errorRetryInteractor() {
        return mock(ErrorRetryInteractor.class);
    }

    @Provides
    @Singleton
    static ErrorStatusReporter errorStatusReporter() {
        ErrorStatusReporter result = mock(ErrorStatusReporter.class);
        when(result.getNumberOfErrors()).thenReturn(Observable.just(0));
        return result;
    }

    @Binds
    ErrorListInteractor errorListInteractor(FakeErrorListInteractor impl);

    @Provides
    @Singleton
    static FakeErrorListInteractor fakeErrorListInteractor() {
        return new FakeErrorListInteractor();
    }

    @Binds
    LocationEditInteractor locationEditInteractor(FakeLocationEditInteractor impl);

    @Provides
    @Singleton
    static FakeLocationEditInteractor fakeLocationEditInteractor() {
        return new FakeLocationEditInteractor();
    }

    @Binds
    LocationConflictInteractor locationConflictInteractor(FakeLocationConflictInteractor impl);

    @Provides
    @Singleton
    static FakeLocationConflictInteractor fakeLocationConflictInteractor() {
        return new FakeLocationConflictInteractor();
    }

    @Binds
    UnitListInteractor unitListInteractor(FakeUnitListInteractor unitListInteractor);

    @Provides
    @Singleton
    static FakeUnitListInteractor fakeunitListInteractor() {
        return new FakeUnitListInteractor();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<Unit> unitDeleter() {
        return mock(EntityDeleter.class);
    }

    @Binds
    ScaledUnitListInteractor ScaledUnitListInteractor(FakeScaledUnitListInteractor impl);

    @Provides
    @Singleton
    static FakeScaledUnitListInteractor FakeScaledUnitListInteractor() {
        return new FakeScaledUnitListInteractor();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<ScaledUnit> ScaledUnitDeleter() {
        return mock(EntityDeleter.class);
    }

    @Provides
    @Singleton
    static UnitAddInteractor UnitAddInteractor() {
        return mock(UnitAddInteractor.class);
    }

    @Binds
    UnitEditInteractor UnitEditInteractor(FakeUnitEditInteractor impl);

    @Provides
    @Singleton
    static FakeUnitEditInteractor FakeUnitEditInteractor() {
        return new FakeUnitEditInteractor();
    }

    @Binds
    UnitConflictInteractor UnitConflictInteractor(FakeUnitConflictInteractor impl);

    @Provides
    @Singleton
    static FakeUnitConflictInteractor FakeUnitConflictInteractor() {
        return new FakeUnitConflictInteractor();
    }

    @Provides
    @Singleton
    static ScaledUnitAddInteractor ScaledUnitAddInteractor() {
        ScaledUnitAddInteractor result = mock(ScaledUnitAddInteractor.class);
        when(result.getUnits()).thenReturn(Observable.just(UnitsForSelection.generate()));
        return result;
    }

    @Binds
    ScaledUnitEditInteractor ScaledUnitEditInteractor(FakeScaledUnitEditInteractor impl);

    @Provides
    @Singleton
    static FakeScaledUnitEditInteractor FakeScaledUnitEditInteractor() {
        return new FakeScaledUnitEditInteractor();
    }

    @Binds
    ScaledUnitConflictInteractor ScaledUnitConflictInteractor(FakeScaledUnitConflictInteractor impl);

    @Provides
    @Singleton
    static FakeScaledUnitConflictInteractor FakeScaledUnitConflictInteractor() {
        return new FakeScaledUnitConflictInteractor();
    }

    @Provides
    @Singleton
    static FoodAddInteractor FoodAddInteractor() {
        FoodAddInteractor result = mock(FoodAddInteractor.class);
        when(result.getUnits()).thenReturn(Observable.just(ScaledUnitsForSelection.generate()));
        when(result.getLocations()).thenReturn(Observable.just(LocationsForSelection.generate()));
        return result;
    }

    @Binds
    EmptyFoodInteractor EmptyFoodInteractor(FakeEmptyFoodInteractor fake);

    @Provides
    @Singleton
    static FakeEmptyFoodInteractor FakeEmptyFoodInteractor() {
        return new FakeEmptyFoodInteractor();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<Food> foodDeleter() {
        return mock(EntityDeleter.class);
    }

    @Binds
    FoodEditInteractor FoodEditInteractor(FakeFoodEditInteractor fake);

    @Provides
    @Singleton
    static FakeFoodEditInteractor FakeFoodEditInteractor() {
        return new FakeFoodEditInteractor();
    }

    @Binds
    FoodConflictInteractor FoodConflictInteractor(FakeFoodConflictInteractor fake);

    @Provides
    @Singleton
    static FakeFoodConflictInteractor FakeFoodConflictInteractor() {
        return new FakeFoodConflictInteractor();
    }

    @Binds
    FoodByLocationListInteractor FoodByLocationListInteractor(FakeFoodByLocationListInteractor fake);

    @Provides
    @Singleton
    static FakeFoodByLocationListInteractor FakeFoodInLocationInteractor() {
        return new FakeFoodByLocationListInteractor();
    }

    @Binds
    AllPresentFoodListInteractor AllPresentFoodListInteractor(FakeAllPresentFoodListInteractor fake);

    @Provides
    @Singleton
    static FakeAllPresentFoodListInteractor FakeAllPresentFoodListInteractor() {
        return new FakeAllPresentFoodListInteractor();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<FoodItem> foodItemDeleter() {
        return mock(EntityDeleter.class);
    }

    @Binds
    FoodItemListInteractor FoodItemListInteractor(FakeFoodItemListInteractor fake);

    @Provides
    @Singleton
    static FakeFoodItemListInteractor FakeFoodItemListInteractor() {
        return new FakeFoodItemListInteractor();
    }

    @Provides
    @Singleton
    static FoodItemAddInteractor FoodItemAddInteractor() {
        return mock(FoodItemAddInteractor.class);
    }

    @Binds
    FoodItemEditInteractor FoodItemEditInteractor(FakeFoodItemEditInteractor fake);

    @Provides
    @Singleton
    static FakeFoodItemEditInteractor FakeFoodItemEditInteractor() {
        return new FakeFoodItemEditInteractor();
    }

    @Binds
    FoodItemConflictInteractor FoodItemConflictInteractor(FakeFoodItemConflictInteractor impl);

    @Provides
    @Singleton
    static FakeFoodItemConflictInteractor FakeFoodItemConflictInteractor() {
        return new FakeFoodItemConflictInteractor();
    }

    @Binds
    UserListInteractor UserListInteractor(FakeUserListInteractor impl);

    @Provides
    @Singleton
    static FakeUserListInteractor FakeUserListInteractor() {
        return new FakeUserListInteractor();
    }

    @Binds
    UserDeviceListInteractor UserDeviceListInteractor(FakeUserDeviceListInteractor impl);

    @Provides
    @Singleton
    static FakeUserDeviceListInteractor FakeUserDeviceListInteractor() {
        return new FakeUserDeviceListInteractor();
    }

    @Binds
    RecipeListInteractor RecipeListInteractor(FakeRecipeListInteractor impl);

    @Provides
    @Singleton
    static FakeRecipeListInteractor FakeRecipeListInteractor() {
        return new FakeRecipeListInteractor();
    }

    @Provides
    @Singleton
    static AccountInformationInteractor AccountInformationInteractor() {
        return () -> Observable.just(AccountInformation.create(
                "Jack",
                "Mobile",
                "stocks.example"
        ));
    }

    @Binds
    EanNumberListInteractor EanNumberListInteractor(FakeEanNumberListInteractor impl);

    @Provides
    @Singleton
    static FakeEanNumberListInteractor FakeEanNumberListInteractor() {
        return new FakeEanNumberListInteractor();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<EanNumber> eanNumberDeleter() {
        return mock(EntityDeleter.class);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<UserDevice> UserDeviceDeleter() {
        return mock(EntityDeleter.class);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unchecked")
    static EntityDeleter<User> UserDeleter() {
        return mock(EntityDeleter.class);
    }

    @Provides
    @Singleton
    static RecipeAddInteractor RecipeAddInteractor() {
        return mock(RecipeAddInteractor.class);
    }

    @Provides
    @Singleton
    static SettingsInteractor SettingsInteractor() {
        return mock(SettingsInteractor.class);
    }

    @Provides
    @Singleton
    static CrashListInteractor CrashListInteractor() {
        return mock(CrashListInteractor.class);
    }

    @Provides
    @Singleton
    static SearchInteractor SearchInteractor() {
        SearchInteractor result = mock(SearchInteractor.class);
        when(result.get(any())).thenReturn(Observable.empty());
        return result;
    }

    @Provides
    @Singleton
    static FoodToBuyInteractor FoodToBuyInteractor() {
        return mock(FoodToBuyInteractor.class);
    }

    @Provides
    @Singleton
    static EventInteractor EventInteractor() {
        EventInteractor result = mock(EventInteractor.class);
        when(result.getNewEventNotifier()).thenReturn(Observable.empty());
        when(result.getEventsOf(any())).thenReturn(Single.just(ActivityEventPage.create(emptyList(), Optional.empty(), Optional.empty())));
        return result;
    }

    @Provides
    @Singleton
    static UnitEventInteractor UnitEventInteractor() {
        UnitEventInteractor result = mock(UnitEventInteractor.class);
        when(result.getNewEventNotifier()).thenReturn(Observable.empty());
        when(result.getEventsOf(any())).thenReturn(Single.just(ActivityEventPage.create(emptyList(), Optional.empty(), Optional.empty())));
        return result;
    }

    @Provides
    @Singleton
    static EventInteractorFactory EventInteractorFactory() {
        var result = mock(EventInteractorFactory.class);
        when(result.forFood(any())).thenReturn(EventInteractor());
        when(result.forLocation(any())).thenReturn(EventInteractor());
        return result;
    }

    @Provides
    @Singleton
    static EanNumberLookupInteractor EanNumberLookupInteractor() {
        return mock(EanNumberLookupInteractor.class);
    }

    @Provides
    @Singleton
    static EanNumberAssignmentInteractor EanNumberAssignmentInteractor() {
        return mock(EanNumberAssignmentInteractor.class);
    }

    @Provides
    @Singleton
    static RecipeDetailInteractor RecipeDetailInteractor() {
        return mock(RecipeDetailInteractor.class);
    }
}
