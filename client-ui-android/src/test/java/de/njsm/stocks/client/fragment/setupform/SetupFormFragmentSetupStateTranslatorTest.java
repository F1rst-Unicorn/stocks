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

package de.njsm.stocks.client.fragment.setupform;

import de.njsm.stocks.client.business.entities.SetupState;
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SetupFormFragmentSetupStateTranslatorTest {

    private SetupFormFragment.SetupStateTranslator uut;

    @Before
    public void setUp() {
        uut = new SetupFormFragment.SetupStateTranslator();
    }

    @Test
    public void stringsAreMappedCorrectly() {
        assertThat(SetupState.GENERATING_KEYS.accept(uut, null), is(R.string.dialog_generating_key));
        assertThat(SetupState.FETCHING_CERTIFICATE.accept(uut, null), is(R.string.dialog_fetching_certificate));
        assertThat(SetupState.VERIFYING_CERTIFICATE.accept(uut, null), is(R.string.dialog_verifying_certificate));
        assertThat(SetupState.REGISTERING_KEY.accept(uut, null), is(R.string.dialog_registering_key));
        assertThat(SetupState.STORING_SETTINGS.accept(uut, null), is(R.string.dialog_storing_settings));
        assertThat(SetupState.GENERATING_KEYS_FAILED.accept(uut, null), is(R.string.dialog_generating_key_failed));
        assertThat(SetupState.FETCHING_CERTIFICATE_FAILED.accept(uut, null), is(R.string.dialog_fetching_certificate_failed));
        assertThat(SetupState.VERIFYING_CERTIFICATE_FAILED.accept(uut, null), is(R.string.dialog_verifying_certificate_failed));
        assertThat(SetupState.REGISTERING_KEY_FAILED.accept(uut, null), is(R.string.dialog_registering_key_failed));
        assertThat(SetupState.STORING_SETTINGS_FAILED.accept(uut, null), is(R.string.dialog_storing_settings_failed));
        assertThat(SetupState.SUCCESS.accept(uut, null), is(R.string.dialog_success));
    }
}
