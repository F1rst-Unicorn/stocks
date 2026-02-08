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

package de.njsm.stocks.server.v2.web;


import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.Health;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.HealthManager;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("/health")
@RestController
@RequestScope
public class HealthEndpoint {

    private final HealthManager healthManager;

    public HealthEndpoint(HealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON)
    public DataResponse<Health> getStatus() {
        Validation<StatusCode, Health> health = healthManager.get();

        if (health.isFail()) {
            return new DataResponse<>(health);
        } else {
            DataResponse<Health> result = new DataResponse<>(health.success().toValidation());
            result.data = health.success();
            return result;
        }
    }
}
