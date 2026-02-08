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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import de.njsm.stocks.server.v2.business.EanNumberManager;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.common.api.StatusCode.INVALID_ARGUMENT;
import static de.njsm.stocks.common.api.StatusCode.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EanNumberEndpointTest {

    private EanNumberEndpoint uut;

    private EanNumberManager manager;

    @BeforeEach
    public void setup() {
        manager = Mockito.mock(EanNumberManager.class);
        uut = new EanNumberEndpoint(manager);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void puttingNullCodeIsInvalid() {

        Response result = uut.putEanNumber(null, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void puttingEmptyCodeIsInvalid() {

        Response result = uut.putEanNumber("", 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void puttingInvalidFoodIdIsInvalid() {

        Response result = uut.putEanNumber("code", 0);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidIdIsInvalid() {

        Response result = uut.delete(0, 1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void deletingInvalidVersionIsInvalid() {

        Response result = uut.delete(1, -1);

        assertEquals(INVALID_ARGUMENT, result.getStatus());
    }

    @Test
    public void eanNumberIsAdded() {
        EanNumberForInsertion data = EanNumberForInsertion.builder()
                .eanNumber("CODE")
                .identifiesFood(2)
                .build();
        when(manager.addReturningId(data)).thenReturn(Validation.success(1));

        Response response = uut.putEanNumber(data.eanNumber(), data.identifiesFood());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).addReturningId(data);
    }

    @Test
    public void getEanNumberReturnsList() {
        List<EanNumber> data = Collections.singletonList(BitemporalEanNumber.builder()
                .id(1)
                .version(2)
                .validTimeStart(Instant.EPOCH)
                .validTimeEnd(Instant.EPOCH)
                .transactionTimeStart(Instant.EPOCH)
                .transactionTimeEnd(Instant.EPOCH)
                .initiates(3)
                .identifiesFood(4)
                .eanNumber("CODE")
                .build());
        when(manager.get(Instant.EPOCH, Instant.EPOCH)).thenReturn(Validation.success(data));

        var actual = (ListResponse<EanNumber>) uut.get(InstantSerialiser.serialize(Instant.EPOCH), InstantSerialiser.serialize(Instant.EPOCH));

        assertEquals(SUCCESS, actual.getStatus());
        assertEquals(data, actual.getData());
        verify(manager).get(Instant.EPOCH, Instant.EPOCH);
    }

    @Test
    public void getEanNumbersFromInvalidStartingPoint() {

        var actual = uut.get("invalid", "invalid");

        assertEquals(INVALID_ARGUMENT, actual.getStatus());
    }

    @Test
    public void deleteWorks() {
        EanNumberForDeletion data = EanNumberForDeletion.builder()
                .id(1)
                .version(0)
                .build();
        when(manager.delete(data)).thenReturn(SUCCESS);

        Response response = uut.delete(data.id(), data.version());

        assertEquals(SUCCESS, response.getStatus());
        verify(manager).delete(data);
    }
}
