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

package de.njsm.stocks.android.business.data.activity;

public interface EventVisitor<I, O> {

    default O visit(EntityEvent<?> e, I arg) {
        return e.accept(this, arg);
    }

    default O newFoodEvent(NewFoodEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O changedFoodEvent(ChangedFoodEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedFoodEvent(DeletedFoodEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newFoodItemEvent(NewFoodItemEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O changedFoodItemEvent(ChangedFoodItemEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedFoodItemEvent(DeletedFoodItemEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newUserEvent(NewUserEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedUserEvent(DeletedUserEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newUserDeviceEvent(NewUserDeviceEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedUserDeviceEvent(DeletedUserDeviceEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newLocationEvent(NewLocationEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O changedLocationEvent(ChangedLocationEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedLocationEvent(DeletedLocationEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newEanNumberEvent(NewEanNumberEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedEanNumberEvent(DeletedEanNumberEvent e, I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newUnitEvent(I arg) {
        throw new RuntimeException("not implemented");
    }

    default O changedUnitEvent(I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedUnitEvent(I arg) {
        throw new RuntimeException("not implemented");
    }

    default O newScaledUnitEvent(I arg) {
        throw new RuntimeException("not implemented");
    }

    default O changedScaledUnitEvent(I arg) {
        throw new RuntimeException("not implemented");
    }

    default O deletedScaledUnitEvent(I arg) {
        throw new RuntimeException("not implemented");
    }
}
