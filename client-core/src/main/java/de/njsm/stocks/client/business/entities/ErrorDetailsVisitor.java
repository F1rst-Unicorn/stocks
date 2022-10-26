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

package de.njsm.stocks.client.business.entities;

public interface ErrorDetailsVisitor<I, O> {

    default O visit(ErrorDetails errorDetails, I input) {
        return errorDetails.accept(this, input);
    }

    O locationAddForm(LocationAddForm locationAddForm, I input);

    O synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, I input);

    O locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, I input);

    O locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, I input);

    O unitAddForm(UnitAddForm unitAddForm, I input);

    O unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, I input);

    O unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, I input);

    O scaledUnitAddErrorDetails(ScaledUnitAddErrorDetails scaledUnitAddErrorDetails, I input);

    O scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, I input);

    O scaledUnitDeleteErrorDetails(ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails, I input);

    O foodAddErrorDetails(FoodAddErrorDetails foodAddErrorDetails, I input);

    O foodDeleteErrorDetails(FoodDeleteErrorDetails foodDeleteErrorDetails, I input);

    O foodEditErrorDetails(FoodEditErrorDetails foodEditErrorDetails, I input);

    O foodItemAddErrorDetails(FoodItemAddErrorDetails foodItemAddErrorDetails, I input);

    O foodItemDeleteErrorDetails(FoodItemDeleteErrorDetails foodItemDeleteErrorDetails, I input);

    interface Default<I, O> extends ErrorDetailsVisitor<I, O> {

        O defaultImpl(ErrorDetails errorDetails, I input);

        @Override
        default O locationAddForm(LocationAddForm locationAddForm, I input) {
            return defaultImpl(locationAddForm, input);
        }

        @Override
        default O synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, I input) {
            return defaultImpl(synchronisationErrorDetails, input);
        }

        @Override
        default O locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, I input) {
            return defaultImpl(locationDeleteErrorDetails, input);
        }

        @Override
        default O locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, I input) {
            return defaultImpl(locationEditErrorDetails, input);
        }

        @Override
        default O unitAddForm(UnitAddForm unitAddForm, I input) {
            return defaultImpl(unitAddForm, input);
        }

        @Override
        default O unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, I input) {
            return defaultImpl(unitDeleteErrorDetails, input);
        }

        @Override
        default O unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, I input) {
            return defaultImpl(unitEditErrorDetails, input);
        }

        @Override
        default O scaledUnitAddErrorDetails(ScaledUnitAddErrorDetails scaledUnitAddErrorDetails, I input) {
            return defaultImpl(scaledUnitAddErrorDetails, input);
        }

        @Override
        default O scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, I input) {
            return defaultImpl(scaledUnitEditErrorDetails, input);
        }

        @Override
        default O scaledUnitDeleteErrorDetails(ScaledUnitDeleteErrorDetails scaledUnitDeleteErrorDetails, I input) {
            return defaultImpl(scaledUnitDeleteErrorDetails, input);
        }

        @Override
        default O foodAddErrorDetails(FoodAddErrorDetails foodAddErrorDetails, I input) {
            return defaultImpl(foodAddErrorDetails, input);
        }

        @Override
        default O foodDeleteErrorDetails(FoodDeleteErrorDetails foodDeleteErrorDetails, I input) {
            return defaultImpl(foodDeleteErrorDetails, input);
        }

        @Override
        default O foodEditErrorDetails(FoodEditErrorDetails foodEditErrorDetails, I input) {
            return defaultImpl(foodEditErrorDetails, input);
        }

        @Override
        default O foodItemAddErrorDetails(FoodItemAddErrorDetails foodItemAddErrorDetails, I input) {
            return defaultImpl(foodItemAddErrorDetails, input);
        }

        @Override
        default O foodItemDeleteErrorDetails(FoodItemDeleteErrorDetails foodItemDeleteErrorDetails, I input) {
            return defaultImpl(foodItemDeleteErrorDetails, input);
        }
    }
}
