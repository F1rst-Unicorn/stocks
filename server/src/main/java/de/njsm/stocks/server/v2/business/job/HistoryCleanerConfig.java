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

import de.njsm.stocks.server.v2.db.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import javax.sql.DataSource;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration
class HistoryCleanerConfig {

    private static final Logger LOG = LogManager.getLogger(HistoryCleanerConfig.class);

    @Bean
    ConnectionFactory historyCleanerConnectionFactory(@Qualifier("hikari") DataSource dataSource) {
        return new ConnectionFactory(dataSource);
    }

    @Bean
    PrincipalsHandler historyCleanerPrincipalsHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new PrincipalsHandler(c);
    }

    @Bean
    FoodHandler historyCleanerFoodHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new FoodHandler(c);
    }

    @Bean
    EanNumberHandler historyCleanerEanNumberHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new EanNumberHandler(c);
    }

    @Bean
    LocationHandler historyCleanerLocationHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c,
            @Qualifier("historyCleanerFoodItemHandler") FoodItemHandler foodItemHandler
    ) {
        return new LocationHandler(c, foodItemHandler);
    }

    @Bean
    FoodItemHandler historyCleanerFoodItemHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c,
            @Qualifier("historyCleanerUserDeviceHandler") UserDeviceHandler userDeviceHandler,
            @Qualifier("historyCleanerUserHandler") UserHandler userHandler
    ) {
        return new FoodItemHandler(c, userDeviceHandler, userHandler);
    }

    @Bean
    UserDeviceHandler historyCleanerUserDeviceHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new UserDeviceHandler(c);
    }

    @Bean
    UserHandler historyCleanerUserHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new UserHandler(c);
    }

    @Bean
    UnitHandler historyCleanerUnitHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new UnitHandler(c);
    }

    @Bean
    ScaledUnitHandler historyCleanerScaledUnitHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new ScaledUnitHandler(c);
    }

    @Bean
    RecipeIngredientHandler historyCleanerRecipeIngredientHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new RecipeIngredientHandler(c);
    }

    @Bean
    RecipeProductHandler historyCleanerRecipeProductHandlerHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new RecipeProductHandler(c);
    }

    @Bean
    RecipeHandler historyCleanerRecipeHandler(
            @Qualifier("historyCleanerConnectionFactory") ConnectionFactory c
    ) {
        return new RecipeHandler(c);
    }

    @Bean
    Period maxHistory(
            @Value("${de.njsm.stocks.server.history-max-period}") String maxHistory
    ) {
        try {
            return Period.parse(maxHistory);
        } catch (DateTimeParseException e) {
            LOG.error("maxPeriod is not a valid ISO-8601 period", e);
            throw e;
        }
    }

    @Bean
    HistoryCleaner historyCleaner(
            @Qualifier("maxHistory") Period maxHistory,
            @Qualifier("historyCleanerPrincipalsHandler") PrincipalsHandler dbHandler,
            @Qualifier("historyCleanerUserHandler") UserHandler userHandler,
            @Qualifier("historyCleanerUserDeviceHandler") UserDeviceHandler userDeviceHandler,
            @Qualifier("historyCleanerFoodItemHandler") FoodItemHandler foodItemHandler,
            @Qualifier("historyCleanerLocationHandler") LocationHandler locationHandler,
            @Qualifier("historyCleanerEanNumberHandler") EanNumberHandler eanNumberHandler,
            @Qualifier("historyCleanerFoodHandler") FoodHandler foodHandler,
            @Qualifier("historyCleanerUnitHandler") UnitHandler unitHandler,
            @Qualifier("historyCleanerScaledUnitHandler") ScaledUnitHandler scaledUnitHandler,
            @Qualifier("historyCleanerRecipeIngredientHandler") RecipeIngredientHandler recipeIngredientHandler,
            @Qualifier("historyCleanerRecipeProductHandlerHandler") RecipeProductHandler recipeProductHandler,
            @Qualifier("historyCleanerRecipeHandler") RecipeHandler recipeHandler,
            AuthenticationManager authenticationManager
    ) {
        return new HistoryCleaner(
                maxHistory,
                dbHandler,
                List.of(
                        userDeviceHandler,
                        userHandler,
                        foodHandler,
                        foodItemHandler,
                        locationHandler,
                        eanNumberHandler,
                        unitHandler,
                        scaledUnitHandler,
                        recipeIngredientHandler,
                        recipeProductHandler,
                        recipeHandler
                ),
                authenticationManager
        );
    }
}
