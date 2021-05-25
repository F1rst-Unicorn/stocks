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

package de.njsm.stocks.android.frontend.main;

import androidx.navigation.NavDirections;
import de.njsm.stocks.android.business.data.activity.*;

import java.util.Optional;

public class OutlineEventVisitor implements EventVisitor<Void, Optional<NavDirections>> {

    public Optional<NavDirections> visit(EntityEvent<?> event) {
        return visit(event, null);
    }

    @Override
    public Optional<NavDirections> newFoodEvent(NewFoodEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItem(e.getEntity().id));
    }

    @Override
    public Optional<NavDirections> changedFoodEvent(ChangedFoodEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItem(e.getEntity().id));
    }

    @Override
    public Optional<NavDirections> deletedFoodEvent(DeletedFoodEvent e, Void arg) {
        return Optional.empty();
    }

    @Override
    public Optional<NavDirections> newFoodItemEvent(NewFoodItemEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItem(e.getEntity().getOfType()));
    }

    @Override
    public Optional<NavDirections> changedFoodItemEvent(ChangedFoodItemEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItem(e.getEntity().getOfType()));
    }

    @Override
    public Optional<NavDirections> deletedFoodItemEvent(DeletedFoodItemEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFoodItem(e.getEntity().getOfType()));
    }

    @Override
    public Optional<NavDirections> newUserEvent(NewUserEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentDevices(e.getEntity().id));
    }

    @Override
    public Optional<NavDirections> deletedUserEvent(DeletedUserEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentUsers());
    }

    @Override
    public Optional<NavDirections> newUserDeviceEvent(NewUserDeviceEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentDevices(e.getEntity().userId));
    }

    @Override
    public Optional<NavDirections> deletedUserDeviceEvent(DeletedUserDeviceEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentDevices(e.getEntity().userId));
    }

    @Override
    public Optional<NavDirections> newLocationEvent(NewLocationEvent e, Void arg) {
        OutlineFragmentDirections.ActionNavFragmentOutlineToNavFragmentFood result = OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFood();
        result.setLocation(e.getEntity().id);
        return Optional.of(result);
    }

    @Override
    public Optional<NavDirections> changedLocationEvent(ChangedLocationEvent e, Void arg) {
        OutlineFragmentDirections.ActionNavFragmentOutlineToNavFragmentFood result = OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentFood();
        result.setLocation(e.getEntity().id);
        return Optional.of(result);
    }

    @Override
    public Optional<NavDirections> deletedLocationEvent(DeletedLocationEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentLocations());
    }

    @Override
    public Optional<NavDirections> newEanNumberEvent(NewEanNumberEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentEanNumber(e.getEntity().identifiesFood));
    }

    @Override
    public Optional<NavDirections> deletedEanNumberEvent(DeletedEanNumberEvent e, Void arg) {
        return Optional.of(OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentEanNumber(e.getEntity().identifiesFood));
    }

    @Override
    public Optional<NavDirections> newUnitEvent(Void arg) {
        return Optional.of(OutlineFragmentDirections.actionGlobalNavFragmentUnits());
    }

    @Override
    public Optional<NavDirections> changedUnitEvent(Void arg) {
        return Optional.of(OutlineFragmentDirections.actionGlobalNavFragmentUnits());
    }

    @Override
    public Optional<NavDirections> deletedUnitEvent(Void arg) {
        return Optional.of(OutlineFragmentDirections.actionGlobalNavFragmentUnits());
    }
}
