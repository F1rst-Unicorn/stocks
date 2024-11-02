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

package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fj.data.Validation;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        creatorVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class Health {

    private final boolean database;

    private final boolean ca;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Health(
            @JsonProperty("database") boolean database,
            @JsonProperty("ca") boolean ca) {

        this.database = database;
        this.ca = ca;
    }

    public boolean isDatabase() {
        return database;
    }

    public boolean isCa() {
        return ca;
    }

    public Validation<StatusCode, Health> toValidation() {
        if (!ca && !database) {
            return Validation.fail(StatusCode.GENERAL_ERROR);
        } else if (!ca) {
            return Validation.fail(StatusCode.CA_UNREACHABLE);
        } else if (!database) {
            return Validation.fail(StatusCode.DATABASE_UNREACHABLE);
        } else {
            return Validation.success(this);
        }
    }
}
