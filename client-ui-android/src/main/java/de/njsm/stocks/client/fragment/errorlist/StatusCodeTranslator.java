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

package de.njsm.stocks.client.fragment.errorlist;

import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.StatusCodeVisitor;
import de.njsm.stocks.client.ui.R;

public class StatusCodeTranslator implements StatusCodeVisitor<Void, Integer> {

    @Override
    public Integer success(StatusCode statusCode, Void input) {
        return R.string.statuscode_success_error_list;
    }

    @Override
    public Integer generalError(StatusCode statusCode, Void input) {
        return R.string.statuscode_general_error_error_list;
    }

    @Override
    public Integer notFound(StatusCode statusCode, Void input) {
        return R.string.statuscode_not_found_error_list;
    }

    @Override
    public Integer invalidDataVersion(StatusCode statusCode, Void input) {
        return R.string.statuscode_invalid_data_version_error_list;
    }

    @Override
    public Integer foreignKeyConstraintViolation(StatusCode statusCode, Void input) {
        return R.string.statuscode_foreign_key_constraint_violation_error_list;
    }

    @Override
    public Integer databaseUnreachable(StatusCode statusCode, Void input) {
        return R.string.statuscode_database_unreachable_error_list;
    }

    @Override
    public Integer accessDenied(StatusCode statusCode, Void input) {
        return R.string.statuscode_access_denied_error_list;
    }

    @Override
    public Integer invalidArgument(StatusCode statusCode, Void input) {
        return R.string.statuscode_invalid_argument_error_list;
    }

    @Override
    public Integer caUnreachable(StatusCode statusCode, Void input) {
        return R.string.statuscode_ca_unreachable_error_list;
    }

    @Override
    public Integer serialisationConflict(StatusCode statusCode, Void input) {
        return R.string.statuscode_serialisation_conflict_error_list;
    }
}
