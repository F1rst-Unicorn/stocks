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

import de.njsm.stocks.client.business.entities.visitor.SetupStateVisitor;

public enum SetupState {

    GENERATING_KEYS {
        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.generatingKeys(this, input);
        }
    },

    FETCHING_CERTIFICATE {
        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.fetchingCertificate(this, input);
        }
    },

    VERIFYING_CERTIFICATE {
        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.verifyingCertificate(this, input);
        }
    },

    REGISTERING_KEY {
        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.registeringKey(this, input);
        }
    },

    STORING_SETTINGS {
        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.storingSettings(this, input);
        }
    },

    GENERATING_KEYS_FAILED {
        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.generatingKeysFailed(this, input);
        }
    },

    FETCHING_CERTIFICATE_FAILED {
        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.fetchingCertificateFailed(this, input);
        }
    },

    VERIFYING_CERTIFICATE_FAILED {
        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.verifyingCertificateFailed(this, input);
        }
    },

    REGISTERING_KEY_FAILED {
        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.registeringKeyFailed(this, input);
        }
    },

    STORING_SETTINGS_FAILED {
        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.storingSettingsFailed(this, input);
        }
    },

    SUCCESS {
        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input) {
            return setupStateVisitor.success(this, input);
        }
    };

    public boolean isFinal() {
        return true;
    }

    public boolean isSuccessful() {
        return false;
    }

    public abstract <I, O> O accept(SetupStateVisitor<I, O> setupStateVisitor, I input);
}
