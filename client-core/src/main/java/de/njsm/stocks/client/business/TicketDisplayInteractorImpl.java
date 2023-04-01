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

import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.business.entities.UserDevice;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

class TicketDisplayInteractorImpl implements TicketDisplayInteractor {

    private final TicketDisplayRepository repository;

    private final Settings settings;

    @Inject
    TicketDisplayInteractorImpl(TicketDisplayRepository repository, Settings settings) {
        this.repository = repository;
        this.settings = settings;
    }

    @Override
    public Observable<RegistrationForm> getRegistrationFormFor(Id<UserDevice> userDevice) {
        return repository.getRegistrationFormFor(userDevice)
                .map(ticketDataForSharing ->
                    RegistrationForm.builder()
                            .serverName(settings.getServerName())
                            .caPort(settings.getCaPort())
                            .registrationPort(settings.getRegistrationPort())
                            .serverPort(settings.getServerPort())
                            .userId(ticketDataForSharing.userId().id())
                            .userName(ticketDataForSharing.userName())
                            .userDeviceId(userDevice.id())
                            .userDeviceName(ticketDataForSharing.deviceName())
                            .fingerprint(settings.getFingerprint())
                            .ticket(ticketDataForSharing.ticket())
                            .build());
    }
}
