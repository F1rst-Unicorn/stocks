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

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v2.business.data.Health;
import de.njsm.stocks.server.v2.db.HealthHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HealthManager extends BusinessObject {

    private static final Logger LOG = LogManager.getLogger(HealthManager.class);

    private HealthHandler dbBackend;

    private AuthAdmin caBackend;

    public HealthManager(HealthHandler dbBackend,
                         AuthAdmin caBackend) {
        super(dbBackend);
        this.dbBackend = dbBackend;
        this.caBackend = caBackend;
    }

    public Validation<StatusCode, Health> get() {
        StatusCode db = dbBackend.get();
        StatusCode ca = caBackend.getHealth();

        return Validation.success(new Health(db == StatusCode.SUCCESS, ca == StatusCode.SUCCESS));
    }
}
