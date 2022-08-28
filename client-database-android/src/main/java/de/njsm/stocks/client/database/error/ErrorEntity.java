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

package de.njsm.stocks.client.database.error;

import androidx.room.Entity;
import androidx.room.Ignore;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.database.IdFields;

@AutoValue
@Entity(tableName = "error")
public abstract class ErrorEntity implements IdFields {

    static ErrorEntity create(int id, Action action, long dataId, ExceptionType exceptionType, long exceptionId) {
        return new AutoValue_ErrorEntity(id, action, dataId, exceptionType, exceptionId);
    }

    @Ignore
    static ErrorEntity create(Action action, long dataId, ExceptionType exceptionType, long exceptionId) {
        return new AutoValue_ErrorEntity(0, action, dataId, exceptionType, exceptionId);
    }

    abstract Action action();

    abstract long dataId();

    abstract ExceptionType exceptionType();

    abstract long exceptionId();

    enum Action {
        SYNCHRONISATION{
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.synchronisation(this, input);
            }
        },

        ADD_LOCATION{
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.addLocation(this, input);
            }
        },

        DELETE_LOCATION{
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.deleteLocation(this, input);
            }
        },

        EDIT_LOCATION{
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.editLocation(this, input);
            }
        },

        ADD_UNIT {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.addUnit(this, input);
            }
        },
        DELETE_UNIT {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.deleteUnit(this, input);
            }
        },
        EDIT_UNIT {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.editUnit(this, input);
            }
        },
        ADD_SCALED_UNIT {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.addScaledUnit(this, input);
            }
        },
        EDIT_SCALED_UNIT {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.editScaledUnit(this, input);
            }
        },
        DELETE_SCALED_UNIT {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.deleteScaledUnit(this, input);
            }
        },
        ADD_FOOD {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.addFood(this, input);
            }
        },
        DELETE_FOOD {
            @Override
            <I, O> O accept(ActionVisitor<I, O> visitor, I input) {
                return visitor.deleteFood(this, input);
            }
        };

        abstract <I, O> O accept(ActionVisitor<I, O> visitor, I input);
    }

    interface ActionVisitor<I, O> {

        default O visit(Action action, I input) {
            return action.accept(this, input);
        }

        O synchronisation(Action action, I input);

        O addLocation(Action action, I input);

        O deleteLocation(Action action, I input);

        O editLocation(Action action, I input);

        O addUnit(Action action, I input);

        O deleteUnit(Action action, I input);

        O editUnit(Action action, I input);

        O addScaledUnit(Action action, I input);

        O editScaledUnit(Action action, I input);

        O deleteScaledUnit(Action action, I input);

        O addFood(Action action, I input);

        O deleteFood(Action action, I input);
    }

    enum ExceptionType {
        SUBSYSTEM_EXCEPTION {
            @Override
            public <I, O> O accept(ExceptionTypeVisitor<I, O> visitor, I input) {
                return visitor.subsystemException(this, input);
            }
        },

        STATUSCODE_EXCEPTION {
            @Override
            public <I, O> O accept(ExceptionTypeVisitor<I, O> visitor, I input) {
                return visitor.statusCodeException(this, input);
            }
        };

        public abstract <I, O> O accept(ExceptionTypeVisitor<I, O> visitor, I input);
    }

    interface ExceptionTypeVisitor<I, O> {

        default O visit(ExceptionType exceptionType, I input) {
            return exceptionType.accept(this, input);
        }

        O subsystemException(ExceptionType exceptionType, I input);

        O statusCodeException(ExceptionType exceptionType, I input);
    }
}

