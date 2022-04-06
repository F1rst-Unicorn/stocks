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
import de.njsm.stocks.client.ui.R;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatusCodeTranslatorTest {

    private StatusCodeTranslator uut;

    @Before
    public void setUp() {
        uut = new StatusCodeTranslator();
    }

    @Test
    public void successIsTranslated() {
        assertEquals((Integer) R.string.statuscode_success_error_list, uut.visit(StatusCode.SUCCESS, null));
        assertEquals((Integer) R.string.statuscode_general_error_error_list, uut.visit(StatusCode.GENERAL_ERROR, null));
        assertEquals((Integer) R.string.statuscode_not_found_error_list, uut.visit(StatusCode.NOT_FOUND, null));
        assertEquals((Integer) R.string.statuscode_invalid_data_version_error_list, uut.visit(StatusCode.INVALID_DATA_VERSION, null));
        assertEquals((Integer) R.string.statuscode_foreign_key_constraint_violation_error_list, uut.visit(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, null));
        assertEquals((Integer) R.string.statuscode_database_unreachable_error_list, uut.visit(StatusCode.DATABASE_UNREACHABLE, null));
        assertEquals((Integer) R.string.statuscode_access_denied_error_list, uut.visit(StatusCode.ACCESS_DENIED, null));
        assertEquals((Integer) R.string.statuscode_invalid_argument_error_list, uut.visit(StatusCode.INVALID_ARGUMENT, null));
        assertEquals((Integer) R.string.statuscode_ca_unreachable_error_list, uut.visit(StatusCode.CA_UNREACHABLE, null));
        assertEquals((Integer) R.string.statuscode_serialisation_conflict_error_list, uut.visit(StatusCode.SERIALISATION_CONFLICT, null));
    }
}
