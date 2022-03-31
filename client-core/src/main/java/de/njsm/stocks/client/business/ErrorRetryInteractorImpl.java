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

import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;

class ErrorRetryInteractorImpl implements ErrorRetryInteractor, ErrorDetailsVisitor<Void, Void> {

    private final LocationAddInteractor locationAddInteractor;

    private final Synchroniser synchroniser;

    private final ErrorRepository errorRepository;

    @Inject
    ErrorRetryInteractorImpl(LocationAddInteractor locationAddInteractor, Synchroniser synchroniser, ErrorRepository errorRepository) {
        this.locationAddInteractor = locationAddInteractor;
        this.synchroniser = synchroniser;
        this.errorRepository = errorRepository;
    }

    @Override
    public void retry(ErrorDescription errorDescription) {
        visit(errorDescription.errorDetails(), null);
        errorRepository.deleteError(errorDescription);
    }

    @Override
    public Void locationAddForm(LocationAddForm locationAddForm, Void input) {
        locationAddInteractor.addLocation(locationAddForm);
        return null;
    }

    @Override
    public Void synchronisationErrorDetails(SynchronisationErrorDetails synchronisationErrorDetails, Void input) {
        synchroniser.synchronise();
        return null;
    }
}
