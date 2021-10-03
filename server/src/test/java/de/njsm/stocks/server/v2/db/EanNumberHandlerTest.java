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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;

public class EanNumberHandlerTest extends DbTestCase implements CrudOperationsTest<EanNumberRecord, EanNumber> {

    private EanNumberHandler uut;

    @BeforeEach
    public void setup() {
        uut = new EanNumberHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<EanNumber>> result = uut.get(true, Instant.EPOCH);

        BitemporalEanNumber sample = (BitemporalEanNumber) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());

        assertEquals(2, sample.identifiesFood());
        assertEquals(1, sample.initiates());
    }

    @Test
    public void deletingCodesWithoutFoodIsOk() {

        StatusCode result = uut.deleteOwnedByFood(FoodForDeletion.builder()
                .id(1)
                .version(1)
                .build());

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void deletingCodesWorks() {
        long codes = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(1, codes);

        StatusCode result = uut.deleteOwnedByFood(FoodForDeletion.builder()
                        .id(2)
                        .version(1)
                        .build());

        assertEquals(StatusCode.SUCCESS, result);
        codes = uut.get(false, Instant.EPOCH).success().count();
        assertEquals(0, codes);
    }

    @Override
    public CrudDatabaseHandler<EanNumberRecord, EanNumber> getDbHandler() {
        return uut;
    }

    @Override
    public Insertable<EanNumber> getInsertable() {
        return EanNumberForInsertion.builder()
                .identifiesFood(1)
                .eanNumber("Code")
                .build();
    }

    @Override
    public int getNumberOfEntities() {
        return 1;
    }

    @Override
    public EanNumberForDeletion getUnknownEntity() {
        return EanNumberForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public EanNumberForDeletion getWrongVersionEntity() {
        return EanNumberForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public EanNumberForDeletion getValidEntity() {
        return EanNumberForDeletion.builder()
                .id(1)
                .version(0)
                .build();
    }
}
