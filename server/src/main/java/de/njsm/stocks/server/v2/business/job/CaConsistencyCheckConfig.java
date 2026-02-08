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

package de.njsm.stocks.server.v2.business.job;

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.v2.db.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import javax.sql.DataSource;

@Configuration
class CaConsistencyCheckConfig {

    @Bean
    ConnectionFactory caConsistencyCheckConnectionFactory(@Qualifier("hikari") DataSource dataSource) {
        return new ConnectionFactory(dataSource);
    }

    @Bean
    PrincipalsHandler caConsistencyCheckPrincipalsHandler(
            @Qualifier("caConsistencyCheckConnectionFactory") ConnectionFactory c
    ) {
        return new PrincipalsHandler(c);
    }

    @Bean
    UserHandler caConsistencyCheckUserHandler(
            @Qualifier("caConsistencyCheckConnectionFactory") ConnectionFactory c
    ) {
        return new UserHandler(c);
    }

    @Bean
    FoodItemHandler caConsistencyCheckFoodItemHandler(
            @Qualifier("caConsistencyCheckConnectionFactory") ConnectionFactory c,
            @Qualifier("caConsistencyCheckUserDeviceHandler") UserDeviceHandler userDeviceHandler,
            @Qualifier("caConsistencyCheckUserHandler") UserHandler userHandler
    ) {
        return new FoodItemHandler(c, userDeviceHandler, userHandler);
    }

    @Bean
    UserDeviceHandler caConsistencyCheckUserDeviceHandler(
            @Qualifier("caConsistencyCheckConnectionFactory") ConnectionFactory c
    ) {
        return new UserDeviceHandler(c);
    }

    @Bean
    CaConsistencyChecker caConsistencyChecker(
            AuthAdmin authAdmin,
            @Qualifier("caConsistencyCheckPrincipalsHandler") PrincipalsHandler dbHandler,
            @Qualifier("caConsistencyCheckUserDeviceHandler") UserDeviceHandler userDeviceHandler,
            @Qualifier("caConsistencyCheckFoodItemHandler") FoodItemHandler foodItemHandler,
            AuthenticationManager authenticationManager
    ) {
        return new CaConsistencyChecker(
                authAdmin,
                dbHandler,
                userDeviceHandler,
                foodItemHandler,
                authenticationManager
        );
    }
}
