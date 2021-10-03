/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.common.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fj.data.Validation;

public class DataResponse<T> extends Response {

    public T data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public DataResponse(@JsonProperty("status") StatusCode status,
                        @JsonProperty("data") T data) {
        super(status);
        this.data = data;
    }

    public DataResponse(Validation<StatusCode, T> option) {
        if (option.isSuccess()) {
            status = StatusCode.SUCCESS;
            data = option.success();
        } else {
            status = option.fail();
            data = null;
        }
    }

    public T getData() {
        return data;
    }
}
