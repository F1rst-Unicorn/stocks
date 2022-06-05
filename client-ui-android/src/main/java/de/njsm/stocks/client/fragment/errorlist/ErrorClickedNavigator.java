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

package de.njsm.stocks.client.fragment.errorlist;

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.navigation.ErrorListNavigator;

import javax.inject.Inject;

class ErrorClickedNavigator implements ErrorDetailsVisitor.Default<ErrorDescription, Void>, StatusCodeVisitor.Default<ErrorDescription, Void> {

    private final ErrorListNavigator errorListNavigator;

    private final ConflictNavigator conflictNavigator;

    @Inject
    public ErrorClickedNavigator(ErrorListNavigator errorListNavigator, ConflictNavigator conflictNavigator) {
        this.errorListNavigator = errorListNavigator;
        this.conflictNavigator = conflictNavigator;
    }

    @Override
    public Void locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, ErrorDescription input) {
        resolveEditingConflict(input);
        return null;
    }

    @Override
    public Void unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, ErrorDescription input) {
        resolveEditingConflict(input);
        return null;
    }

    @Override
    public Void scaledUnitEditErrorDetails(ScaledUnitEditErrorDetails scaledUnitEditErrorDetails, ErrorDescription input) {
        resolveEditingConflict(input);
        return null;
    }

    @Override
    public Void invalidDataVersion(StatusCode statusCode, ErrorDescription input) {
        conflictNavigator.visit(input.errorDetails(), input);
        return null;
    }

    @Override
    public Void defaultImpl(ErrorDetails errorDetails, ErrorDescription input) {
        showErrorDetails(input);
        return null;
    }

    @Override
    public Void defaultImpl(StatusCode statusCode, ErrorDescription input) {
        showErrorDetails(input);
        return null;
    }

    private void showErrorDetails(ErrorDescription input) {
        errorListNavigator.showErrorDetails(input.id());
    }

    private void resolveEditingConflict(ErrorDescription input) {
        StatusCodeVisitor.Default.super.visit(input.statusCode(), input);
    }
}
