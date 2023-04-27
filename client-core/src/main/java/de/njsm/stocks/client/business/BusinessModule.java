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
import de.njsm.stocks.client.business.event.*;
import de.njsm.stocks.client.execution.ExecutionModule;

import javax.inject.Singleton;

@Module(
        includes = {
                ExecutionModule.class,
        }
)
public interface BusinessModule {

    @Binds
    SetupInteractor setupInteractor(SetupInteractorImpl impl);

    @Binds
    SetupRunner setupRunner(SetupInteractorImpl impl);

    @Provides
    @Singleton
    static SetupInteractorImpl setupInteractorImpl(SettingsWriter settingsWriter,
                                                   CertificateFetcher certificateFetcher,
                                                   Registrator registrator,
                                                   CertificateStore certificateStore,
                                                   KeyGenerator keyPairGenerator) {
        return new SetupInteractorImpl(settingsWriter, certificateFetcher, registrator, certificateStore, keyPairGenerator);
    }

    @Binds
    Synchroniser synchroniser(SynchroniserImpl impl);

    @Binds
    SynchroniseInteractor synchroniseInteractor(SynchroniseInteractorImpl impl);

    @Binds
    EntityDeleter<Location> locationDeleter(LocationDeleterImpl impl);

    @Binds
    LocationListInteractor locationListInteractor(LocationListInteractorImpl impl);

    @Binds
    LocationAddInteractor locationAddInteractor(LocationAddInteractorImpl impl);

    @Binds
    ErrorRetryInteractor errorRetryInteractor(ErrorRetryInteractorImpl impl);

    @Binds
    ErrorListInteractor errorListInteractor(ErrorListInteractorImpl impl);

    @Binds
    ErrorStatusReporter errorStatusReporter(ErrorListInteractorImpl impl);

    @Binds
    LocationEditInteractor locationEditInteractorImpl(LocationEditInteractorImpl impl);

    @Binds
    LocationConflictInteractor locationConflictInteractor(LocationConflictInteractorImpl impl);

    @Binds
    UnitListInteractor unitListInteractor(UnitListInteractorImpl impl);

    @Binds
    EntityDeleter<Unit> UnitDeleter(UnitDeleterImpl impl);

    @Binds
    ScaledUnitListInteractor ScaledUnitListInteractor(ScaledUnitListInteractorImpl impl);

    @Binds
    EntityDeleter<ScaledUnit> ScaledUnitDeleter(ScaledUnitDeleterImpl impl);

    @Binds
    UnitAddInteractor UnitAddInteractor(UnitAddInteractorImpl impl);

    @Binds
    UnitEditInteractor UnitEditInteractor(UnitEditInteractorImpl impl);

    @Binds
    UnitConflictInteractor UnitConflictInteractor(UnitConflictInteractorImpl impl);

    @Binds
    ScaledUnitAddInteractor ScaledUnitAddInteractor(ScaledUnitAddInteractorImpl impl);

    @Binds
    ScaledUnitEditInteractor ScaledUnitEditInteractor(ScaledUnitEditInteractorImpl impl);

    @Binds
    ScaledUnitConflictInteractor ScaledUnitConflictInteractor(ScaledUnitConflictInteractorImpl impl);

    @Binds
    FoodAddInteractor FoodAddInteractor(FoodAddInteractorImpl impl);

    @Binds
    EmptyFoodInteractor EmptyFoodInteractor(EmptyFoodInteractorImpl impl);

    @Binds
    EntityDeleter<Food> FoodDeleter(FoodDeleterImpl impl);

    @Binds
    FoodEditInteractor FoodEditInteractor(FoodEditInteractorImpl impl);

    @Binds
    FoodConflictInteractor FoodConflictInteractor(FoodConflictInteractorImpl impl);

    @Binds
    FoodByLocationListInteractor FoodByLocationListInteractor(FoodByLocationListInteractorImpl impl);

    @Binds
    AllPresentFoodListInteractor AllPresentFoodListInteractor(AllPresentFoodListInteractorImpl impl);

    @Binds
    FoodItemListInteractor FoodItemListInteractor(FoodItemListInteractorImpl impl);

    @Binds
    FoodItemAddInteractor FoodItemAddInteractor(FoodItemAddInteractorImpl impl);

    @Binds
    EntityDeleter<FoodItem> FoodItemDeleter(FoodItemDeleterImpl impl);

    @Binds
    FoodItemEditInteractor FoodItemEditInteractor(FoodItemEditInteractorImpl impl);

    @Binds
    FoodItemConflictInteractor FoodItemConflictInteractor(FoodItemConflictInteractorImpl impl);

    @Binds
    UserListInteractor UserListInteractor(UserListInteractorImpl impl);

    @Binds
    UserDeviceListInteractor UserDeviceListInteractor(UserDeviceListInteractorImpl impl);

    @Binds
    AccountInformationInteractor AccountInformationInteractor(AccountInformationInteractorImpl impl);

    @Binds
    RecipeListInteractor RecipeListInteractor(RecipeListInteractorImpl impl);

    @Binds
    EanNumberListInteractor EanNumberListInteractor(EanNumberListInteractorImpl impl);

    @Binds
    EntityDeleter<EanNumber> EanNumberDeleter(EanNumberDeleterImpl impl);

    @Binds
    EntityDeleter<UserDevice> UserDeviceDeleter(UserDeviceDeleterImpl impl);

    @Binds
    EntityDeleter<User> UserDeleter(UserDeleterImpl impl);

    @Binds
    RecipeAddInteractor RecipeAddInteractor(RecipeAddInteractorImpl impl);

    @Binds
    SettingsInteractor SettingsInteractor(SettingsInteractorImpl impl);

    @Binds
    CrashListInteractor CrashListInteractor(CrashListInteractorImpl impl);

    @Binds
    SearchInteractor SearchInteractor(SearchInteractorImpl impl);

    @Binds
    FoodToBuyInteractor FoodToBuyInteractor(FoodToBuyInteractorImpl impl);

    @Binds
    EventInteractor EventInteractor(EventInteractorImpl impl);

    @Binds
    UnitEventInteractor UnitEventInteractor(UnitEventInteractorImpl impl);

    @Binds
    EventInteractorFactory EventInteractorFactory(EventInteractorFactoryImpl impl);

    @Binds
    EanNumberLookupInteractor EanNumberLookupInteractor(EanNumberLookupInteractorImpl impl);

    @Binds
    EanNumberAssignmentInteractor EanNumberAssignmentInteractor(EanNumberAssignmentInteractorImpl impl);

    @Binds
    RecipeDetailInteractor RecipeDetailInteractor(RecipeDetailInteractorImpl impl);

    @Binds
    UserAddInteractor UserAddInteractor(UserAddInteractorImpl impl);

    @Binds
    UserDeviceAddInteractor UserDeviceAddInteractor(UserDeviceAddInteractorImpl impl);

    @Binds
    TicketDisplayInteractor TicketDisplayInteractor(TicketDisplayInteractorImpl impl);

    @Binds
    FoodDetailsInteractor FoodDetailsInteractor(FoodDetailsInteractorImpl impl);
}
