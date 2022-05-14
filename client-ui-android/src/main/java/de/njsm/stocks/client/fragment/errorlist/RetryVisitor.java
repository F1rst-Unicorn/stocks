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

import javax.inject.Inject;
import java.util.function.Consumer;

class RetryVisitor implements ErrorDetailsVisitor<ErrorDescription, Void>, StatusCodeVisitor<ErrorDescription, Void> {

    private final ConflictNavigator conflictNavigator;

    private Consumer<ErrorDescription> retryDirectlyCallback;

    @Inject
    RetryVisitor(ConflictNavigator conflictNavigator) {
        this.conflictNavigator = conflictNavigator;
        this.retryDirectlyCallback = v -> {
            throw new IllegalStateException("no callback given");
        };
    }

    @Override
    public Void locationEditErrorDetails(LocationEditErrorDetails locationEditErrorDetails, ErrorDescription input) {
        return visit(input.statusCode(), input);
    }

    @Override
    public Void unitAddForm(UnitAddForm unitAddForm, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void unitDeleteErrorDetails(UnitDeleteErrorDetails unitDeleteErrorDetails, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void unitEditErrorDetails(UnitEditErrorDetails unitEditErrorDetails, ErrorDescription input) {
        return visit(input.statusCode(), input);
    }

    @Override
    public Void locationAddForm(LocationAddForm locationAddForm, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void locationDeleteErrorDetails(LocationDeleteErrorDetails locationDeleteErrorDetails, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void success(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void generalError(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void notFound(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void invalidDataVersion(StatusCode statusCode, ErrorDescription input) {
        return conflictNavigator.visit(input.errorDetails(), input);
    }

    @Override
    public Void foreignKeyConstraintViolation(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void databaseUnreachable(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void accessDenied(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void invalidArgument(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void caUnreachable(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    @Override
    public Void serialisationConflict(StatusCode statusCode, ErrorDescription input) {
        retryDirectlyCallback.accept(input);
        return null;
    }

    void setRetryDirectlyCallback(Consumer<ErrorDescription> retryDirectlyCallback) {
        this.retryDirectlyCallback = retryDirectlyCallback;
    }
}
