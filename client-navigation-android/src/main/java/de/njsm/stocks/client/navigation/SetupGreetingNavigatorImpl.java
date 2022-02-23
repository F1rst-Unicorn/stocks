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

import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.view.SetupGreetingFragmentDirections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class SetupGreetingNavigatorImpl implements SetupGreetingNavigator {

    private static final Logger LOG = LoggerFactory.getLogger(SetupGreetingNavigatorImpl.class);

    private final NavigationArgConsumer navigationArgConsumer;

    @Inject
    SetupGreetingNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        this.navigationArgConsumer = navigationArgConsumer;
    }

    @Override
    public void registerManually() {
        LOG.debug("going to manual setup");
        navigationArgConsumer.navigate(
                SetupGreetingFragmentDirections.actionNavFragmentSetupGreetingToNavFragmentSetupForm());
    }

    @Override
    public void registerWithPrefilledData(RegistrationForm registrationForm) {
        LOG.debug("going to prefilled setup with " + registrationForm);
        SetupGreetingFragmentDirections.ActionNavFragmentSetupGreetingToNavFragmentSetupForm direction =
                SetupGreetingFragmentDirections.actionNavFragmentSetupGreetingToNavFragmentSetupForm()
                        .setRegistrationForm(registrationForm);
        navigationArgConsumer.navigate(direction);
    }
}
