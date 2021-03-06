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

package de.njsm.stocks.android.network.server;

import de.njsm.stocks.R;
import de.njsm.stocks.android.util.Logger;

public enum StatusCode {


    SUCCESS,

    GENERAL_ERROR,

    NOT_FOUND,

    INVALID_DATA_VERSION,

    FOREIGN_KEY_CONSTRAINT_VIOLATION,

    DATABASE_UNREACHABLE,

    ACCESS_DENIED,

    INVALID_ARGUMENT,

    CA_UNREACHABLE,

    SERIALISATION_CONFLICT;

    private static final Logger LOG = new Logger(StatusCode.class);

    public int getReadErrorMessage() {
        switch (this) {
            case SUCCESS:
                LOG.e("Tried to notify for successful call");
                return R.string.dialog_done;
            case GENERAL_ERROR:
                return R.string.dialog_error;
            case INVALID_ARGUMENT:
            case NOT_FOUND:
            case INVALID_DATA_VERSION:
            case FOREIGN_KEY_CONSTRAINT_VIOLATION:
            case ACCESS_DENIED:
            case SERIALISATION_CONFLICT:
                LOG.e("Server should not reply with " + name() + " when reading");
                return R.string.dialog_done;
            case DATABASE_UNREACHABLE:
            case CA_UNREACHABLE:
                LOG.e("Server trouble: " + name());
                return R.string.dialog_server_trouble;
        }
        LOG.e("Didn't catch code " + name());
        return R.string.dialog_done;
    }

    public int getAddErrorMessage() {
        switch (this) {
            case SUCCESS:
                LOG.e("Tried to notify for successful call");
                return R.string.dialog_done;
            case GENERAL_ERROR:
                return R.string.dialog_error;
            case INVALID_ARGUMENT:
                LOG.e("Input validation was wrong for input");
                return R.string.dialog_invalid_input;
            case NOT_FOUND:
            case INVALID_DATA_VERSION:
            case FOREIGN_KEY_CONSTRAINT_VIOLATION:
            case ACCESS_DENIED:
            case SERIALISATION_CONFLICT:
                LOG.e("Server should not reply with " + name() + " when adding");
                return R.string.dialog_done;
            case DATABASE_UNREACHABLE:
            case CA_UNREACHABLE:
                LOG.e("Server trouble: " + name());
                return R.string.dialog_server_trouble;
        }
        LOG.e("Didn't catch code " + name());
        return R.string.dialog_done;
    }

    public int getEditErrorMessage() {
        switch (this) {
            case SUCCESS:
                LOG.e("Tried to notify for successful call");
                return R.string.dialog_done;
            case GENERAL_ERROR:
                return R.string.dialog_error;
            case INVALID_ARGUMENT:
                LOG.e("Input validation was wrong for input");
                return R.string.dialog_invalid_input;
            case NOT_FOUND:
                return R.string.dialog_not_found;
            case INVALID_DATA_VERSION:
                return R.string.dialog_edit_but_edited;
            case FOREIGN_KEY_CONSTRAINT_VIOLATION:
            case ACCESS_DENIED:
            case SERIALISATION_CONFLICT:
                LOG.e("Server should not reply with " + name() + " when editing");
                return R.string.dialog_done;
            case DATABASE_UNREACHABLE:
            case CA_UNREACHABLE:
                LOG.e("Server trouble: " + name());
                return R.string.dialog_server_trouble;
        }
        LOG.e("Didn't catch code " + name());
        return R.string.dialog_done;
    }

    public int getDeleteErrorMessage() {
        switch (this) {
            case SUCCESS:
                LOG.e("Tried to notify for successful call");
                return R.string.dialog_done;
            case GENERAL_ERROR:
                return R.string.dialog_error;
            case NOT_FOUND:
                return R.string.dialog_not_found;
            case INVALID_DATA_VERSION:
                return R.string.dialog_delete_but_edited;
            case INVALID_ARGUMENT:
                LOG.e("Input validation was wrong for input");
                return R.string.dialog_invalid_input;
            case FOREIGN_KEY_CONSTRAINT_VIOLATION:
                return R.string.dialog_delete_but_dependants;
            case ACCESS_DENIED:
            case SERIALISATION_CONFLICT:
                LOG.e("Server should not reply with " + name() + " when deleting");
                return R.string.dialog_done;
            case DATABASE_UNREACHABLE:
            case CA_UNREACHABLE:
                LOG.e("Server trouble: " + name());
                return R.string.dialog_server_trouble;
        }
        LOG.e("Didn't catch code " + name());
        return R.string.dialog_done;
    }
}
