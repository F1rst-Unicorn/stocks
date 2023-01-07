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

import android.os.Bundle;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragment;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentArgs;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentArgumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

class SetupFormFragmentArgumentProviderImpl implements SetupFormFragmentArgumentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SetupFormFragmentArgumentProviderImpl.class);

    @Inject
    public SetupFormFragmentArgumentProviderImpl() {
    }

    @Override
    public void visit(SetupFormFragment fragment, Bundle bundle) {
        Optional.ofNullable(bundle)
                .map(SetupFormFragmentArgs::fromBundle)
                .map(SetupFormFragmentArgs::getRegistrationForm)
                .ifPresent(fragment::initialiseForm);
    }
}
