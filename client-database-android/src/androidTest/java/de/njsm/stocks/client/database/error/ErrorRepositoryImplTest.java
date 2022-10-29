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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.ErrorRepository;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.database.DbTestCase;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;

public class ErrorRepositoryImplTest extends DbTestCase {

    ErrorRecorder errorRecorder;

    private ErrorRepository uut;

    @Before
    public void setup() {
        errorRecorder = new ErrorRecorderImpl(stocksDatabase.errorDao(), this);
        uut = new ErrorRepositoryImpl(stocksDatabase.errorDao(), localiser);
    }

    @Test
    public void noErrorsInitially() {
        test(uut.getErrors()).assertValue(List::isEmpty);
        test(uut.getNumberOfErrors()).assertValue(0);
    }

    @Test
    public void singleErrorCanBeRetrievedById() {
        SubsystemException exception = new SubsystemException("test");
        errorRecorder.recordSynchronisationError(exception);
        ErrorDescription input = testList(uut.getErrors()).values().get(0).get(0);

        Observable<ErrorDescription> actual = uut.getError(input.id());

        test(actual).assertValue(input);
    }
}
