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

package de.njsm.stocks.client.navigation;

import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.event.*;
import de.njsm.stocks.client.fragment.outline.OutlineFragmentDirections;

import javax.inject.Inject;

class OutlineNavigatorImpl extends BaseNavigator implements OutlineNavigator {

    @Inject
    OutlineNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void addFood() {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentAddFood());
    }

    @Override
    public void showAllFood() {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentAllFood());
    }

    @Override
    public void showEmptyFood() {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentEmptyFood());
    }

    @Override
    public void showFood(Id<Food> foodId) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItemTabs(foodId.id())
        );
    }

    @Override
    public void showAllFoodForEanNumber(String eanNumber) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodEanAssignment(eanNumber)
        );
    }

    @Override
    public Void userCreated(UserCreatedEvent userCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUsers()
        );
        return null;
    }

    @Override
    public Void userDeleted(UserDeletedEvent userDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUsers()
        );
        return null;
    }

    @Override
    public Void userDeviceCreated(UserDeviceCreatedEvent userDeviceCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentDeviceList(userDeviceCreatedEvent.ownerId().id())
        );
        return null;
    }

    @Override
    public Void userDeviceDeleted(UserDeviceDeletedEvent userDeviceDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentDeviceList(userDeviceDeletedEvent.ownerId().id())
        );
        return null;
    }

    @Override
    public Void locationCreated(LocationCreatedEvent locationCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentLocationContent(locationCreatedEvent.id().id())
        );
        return null;
    }

    @Override
    public Void locationDeleted(LocationDeletedEvent locationDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentLocationList()
        );
        return null;
    }

    @Override
    public Void locationEdited(LocationEditedEvent locationEditedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentLocationContent(locationEditedEvent.id().id())
        );
        return null;
    }

    @Override
    public Void foodCreated(FoodCreatedEvent foodCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItemTabs(foodCreatedEvent.id().id())
        );
        return null;
    }

    @Override
    public Void foodDeletedEvent(FoodDeletedEvent foodDeletedEvent, Void input) {
        // nothing to show
        return null;
    }

    @Override
    public Void foodEditedEvent(FoodEditedEvent foodEditedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItemTabs(foodEditedEvent.id().id())
        );
        return null;
    }

    @Override
    public Void foodItemCreated(FoodItemCreatedEvent foodItemCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItemTabs(foodItemCreatedEvent.ofType().id())
        );
        return null;
    }

    @Override
    public Void foodItemDeleted(FoodItemDeletedEvent foodItemDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItemTabs(foodItemDeletedEvent.ofType().id())
        );
        return null;
    }

    @Override
    public Void foodItemEdited(FoodItemEditedEvent foodItemEditedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItemTabs(foodItemEditedEvent.ofType().id())
        );
        return null;
    }

    @Override
    public Void scaledUnitCreated(ScaledUnitCreatedEvent scaledUnitCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUnitTabs()
        );
        return null;
    }

    @Override
    public Void scaledUnitDeleted(ScaledUnitDeletedEvent scaledUnitDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUnitTabs()
        );
        return null;
    }

    @Override
    public Void scaledUnitEdited(ScaledUnitEditedEvent scaledUnitEditedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUnitTabs()
        );
        return null;
    }

    @Override
    public Void unitCreated(UnitCreatedEvent unitCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUnitTabs()
        );
        return null;
    }

    @Override
    public Void unitDeleted(UnitDeletedEvent unitDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUnitTabs()
        );
        return null;
    }

    @Override
    public Void unitEdited(UnitEditedEvent unitEditedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionGlobalNavFragmentUnitTabs()
        );
        return null;
    }

    @Override
    public Void eanNumberCreated(EanNumberCreatedEvent eanNumberCreatedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentEanNumbers(eanNumberCreatedEvent.identifies().id())
        );
        return null;
    }

    @Override
    public Void eanNumberDeleted(EanNumberDeletedEvent eanNumberDeletedEvent, Void input) {
        getNavigationArgConsumer().navigate(
                OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentEanNumbers(eanNumberDeletedEvent.identifies().id())
        );
        return null;
    }
}
