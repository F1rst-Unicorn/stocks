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

public enum StatusCode {

    SUCCESS {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.success(this, input);
        }
    },

    GENERAL_ERROR {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.generalError(this, input);
        }
    },

    NOT_FOUND {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.notFound(this, input);
        }
    },

    INVALID_DATA_VERSION {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.invalidDataVersion(this, input);
        }
    },

    FOREIGN_KEY_CONSTRAINT_VIOLATION {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.foreignKeyConstraintViolation(this, input);
        }
    },

    DATABASE_UNREACHABLE {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.databaseUnreachable(this, input);
        }
    },

    ACCESS_DENIED {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.accessDenied(this, input);
        }
    },

    INVALID_ARGUMENT {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.invalidArgument(this, input);
        }
    },

    CA_UNREACHABLE {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.caUnreachable(this, input);
        }
    },

    SERIALISATION_CONFLICT {
        @Override
        public <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input) {
            return visitor.serialisationConflict(this, input);
        }
    };

    public boolean isFail() {
        return this != SUCCESS;
    }

    public boolean isSuccess() {
        return !isFail();
    }

    public abstract <I, O> O accept(StatusCodeVisitor<I, O> visitor, I input);
}
