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

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Job {

    public static Job create(Type name, Runnable runnable) {
        return new AutoValue_Job(name, runnable);
    }

    public abstract Type name();

    public abstract Runnable runnable();

    @Override
    public String toString() {
        return name().toString();
    }

    public enum Type {
        SETUP {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.setup(this, input);
            }
        },

        DATABASE {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.database(this, input);
            }
        },

        SYNCHRONISATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.synchronisation(this, input);
            }
        },

        ADD_LOCATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addLocation(this, input);
            }
        },

        DELETE_ERROR {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteError(this, input);
            }
        },

        DELETE_LOCATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteLocation(this, input);
            }
        },

        EDIT_LOCATION {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.editLocation(this, input);
            }
        },

        ADD_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.addUnit(this, input);
            }
        },

        DELETE_UNIT {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.deleteUnit(this, input);
            }
        },

        UNKNOWN {
            @Override
            public <I, O> O accept(TypeVisitor<I, O> visitor, I input) {
                return visitor.unknown(this, input);
            }
        };

        public abstract <I, O> O accept(TypeVisitor<I, O> visitor, I input);
    }

    public interface TypeVisitor<I, O> {

        default O visit(Type type, I input) {
            return type.accept(this, input);
        }

        O setup(Type type, I input);

        O database(Type type, I input);

        O synchronisation(Type type, I input);

        O addLocation(Type type, I input);

        O deleteError(Type type, I input);

        O deleteLocation(Type type, I input);

        O editLocation(Type type, I input);

        O unknown(Type type, I input);

        O addUnit(Type type, I input);

        O deleteUnit(Type type, I input);
    }
}
