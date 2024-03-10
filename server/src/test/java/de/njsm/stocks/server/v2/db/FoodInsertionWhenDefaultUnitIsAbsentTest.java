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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.FoodForInsertion;
import de.njsm.stocks.common.api.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FoodInsertionWhenDefaultUnitIsAbsentTest extends DbTestCase {

    private FoodHandler uut;

    @BeforeEach
    public void setup() {
        uut = new FoodHandler(getConnectionFactory());
        uut.setPrincipals(TEST_USER);
    }

    @Override
    protected SampleData getSampleData(Connection connection) {
        return new TayloredSampleData(connection);
    }

    @Test
    void addingFoodWhenDefaultUnitIsMissingWorks() {
        FoodForInsertion food = getInsertable();

        StatusCode insertionResult = uut.add(food);
        StatusCode commitResult = uut.commit();

        assertThat(insertionResult, is(StatusCode.SUCCESS));
        assertThat(commitResult, is(StatusCode.SUCCESS));
    }

    public FoodForInsertion getInsertable() {
        return FoodForInsertion.builder()
                .name("Banana")
                .build();
    }

    private static class TayloredSampleData extends SampleData {
        public TayloredSampleData(Connection connection) {
            super(connection);
        }

        @Override
        List<String> getSampleDbData() {
            return List.of(
                    "insert into \"user\" (name, initiates) values " +
                            "('Default', 1), " +
                            "('Stocks', 1)",
                    "insert into user_device (name, belongs_to, initiates, technical_use_case) values " +
                            "('Default', 1, 1, NULL), " +
                            "('Job Runner', 2, 1, 'job-runner')",
                    "insert into unit (name, abbreviation, initiates) values" +
                            "('Default', 'default', 1)",
                    "update \"user\" set valid_time_start = current_timestamp - interval '1 day', transaction_time_start = current_timestamp - interval '1 day'",
                    "update user_device set valid_time_start = current_timestamp - interval '1 day', transaction_time_start = current_timestamp - interval '1 day'",
                    "update unit set valid_time_start = current_timestamp - interval '1 day', transaction_time_start = current_timestamp - interval '1 day'",
                    "insert into scaled_unit (scale, unit, initiates) values (1, 1, 1)",
                    "insert into scaled_unit (id, \"version\", valid_time_start, valid_time_end, transaction_time_start, transaction_time_end, \"scale\", unit, initiates) values " +
                            "(2, 0, current_timestamp - interval '1 day', 'infinity', current_timestamp - interval '1 day', current_timestamp - interval '1 hour', 1, 1, 1)," +
                            "(2, 0, current_timestamp - interval '1 day', current_timestamp - interval '1 hour', current_timestamp - interval '1 hour', 'infinity', 1, 1, 1)"
            );
        }
    }
}
