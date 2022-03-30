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

public interface StatusCodeVisitor<I, O> {

    O success(StatusCode statusCode, I input);

    O generalError(StatusCode statusCode, I input);

    O notFound(StatusCode statusCode, I input);

    O invalidDataVersion(StatusCode statusCode, I input);

    O foreignKeyConstraintViolation(StatusCode statusCode, I input);

    O databaseUnreachable(StatusCode statusCode, I input);

    O accessDenied(StatusCode statusCode, I input);

    O invalidArgument(StatusCode statusCode, I input);

    O caUnreachable(StatusCode statusCode, I input);

    O serialisationConflict(StatusCode statusCode, I input);
}
