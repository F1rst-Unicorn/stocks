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

public class SubsystemException extends RuntimeException {

    public SubsystemException() {
    }

    public SubsystemException(String message) {
        super(message);
    }

    public SubsystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubsystemException(Throwable cause) {
        super(cause);
    }

    public SubsystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public interface Visitor<I, O> {

        default O visit(SubsystemException exception, I input) {
            return exception.accept(this, input);
        }

        O subsystemException(SubsystemException exception, I input);

        O statusCodeException(StatusCodeException exception, I input);
    }

    <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.subsystemException(this, input);
    }
}
