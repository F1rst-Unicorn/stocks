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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.ErrorDetails;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.SynchronisationErrorDetails;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErrorListInteractorImplTest {

    private ErrorListInteractorImpl uut;

    private ErrorRepository errorRepository;

    @BeforeEach
    void setUp() {
        errorRepository = mock(ErrorRepository.class);
        uut = new ErrorListInteractorImpl(errorRepository);
    }

    @Test
    void errorListIsForwarded() {
        List<ErrorDescription> expected = Collections.emptyList();
        when(errorRepository.getErrors()).thenReturn(Observable.just(expected));

        Observable<List<ErrorDescription>> actual = uut.getErrors();

        actual.test().assertValue(expected);
    }

    @Test
    void singleErrorIsForwarded() {
        ErrorDetails details = SynchronisationErrorDetails.create();
        ErrorDescription expected = ErrorDescription.create(2, StatusCode.GENERAL_ERROR, "", "", details);
        when(errorRepository.getError(expected.id())).thenReturn(Observable.just(expected));

        Observable<ErrorDescription> actual = uut.getError(expected.id());

        actual.test().assertValue(expected);
    }

    @Test
    void counterIsForwarded() {
        int expected = 2;
        when(errorRepository.getNumberOfErrors()).thenReturn(Observable.just(expected));

        Observable<Integer> actual = uut.getNumberOfErrors();

        actual.test().assertValue(expected);
    }
}
