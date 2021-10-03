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

package de.njsm.stocks.android.error;


import de.njsm.stocks.common.api.StatusCode;

public class StatusCodeException extends Exception {

    private StatusCode code;

    public StatusCodeException(StatusCode code) {
        this.code = code;
    }

    public StatusCodeException(String message, StatusCode code) {
        super(message);
        this.code = code;
    }

    public StatusCodeException(String message, Throwable cause, StatusCode code) {
        super(message, cause);
        this.code = code;
    }

    public StatusCodeException(StatusCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public StatusCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, StatusCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public StatusCode getCode() {
        return code;
    }
}
