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

package de.njsm.stocks.server.v2.business;


import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Health;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v2.db.HealthHandler;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthManagerTest {

    private HealthManager uut;

    private HealthHandler db;

    @BeforeEach
    public void setup() {
        db = mock(HealthHandler.class);
        AuthAdmin ca = mock(AuthAdmin.class);
        uut = new HealthManager(db, ca);
    }

    @Test
    public void testGettingHealth() {
        when(db.commit()).thenReturn(StatusCode.SUCCESS);
        Validation<StatusCode, Health> result = uut.get();

        assertTrue(result.isSuccess());
        assertFalse(result.success().isDatabase());
        assertFalse(result.success().isCa());
    }
}
