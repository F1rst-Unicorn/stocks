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

import de.njsm.stocks.client.business.ErrorRepository;
import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.business.entities.ErrorDetails;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.SynchronisationErrorDetails;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.client.database.DataMapper.map;

public class ErrorRepositoryImpl implements ErrorRepository, ErrorEntity.ActionVisitor<Long, ErrorDetails> {

    private final ErrorDao errorDao;

    @Inject
    ErrorRepositoryImpl(ErrorDao errorDao) {
        this.errorDao = errorDao;
    }

    @Override
    public Observable<Integer> getNumberOfErrors() {
        return errorDao.getNumberOfErrors();
    }

    @Override
    public void deleteError(ErrorDescription input) {
        ErrorEntity error = errorDao.getError(input.id());
        errorDao.deleteError(input.id());
        new ExceptionDeleter(errorDao).visit(error.exceptionType(), error.exceptionId());
        new DataDeleter(errorDao).visit(error.action(), error.dataId());
    }

    @Override
    public Observable<List<ErrorDescription>> getErrors() {
        return errorDao.observeErrors()
                .map(v -> v.stream().map(this::resolveData).collect(Collectors.toList()));
    }

    private ErrorDescription resolveData(ErrorEntity errorEntity) {
        ErrorDetails errorDetails = visit(errorEntity.action(), errorEntity.dataId());
        StatusCode statusCode = new ExceptionStatusCodeLoader(errorDao).visit(errorEntity.exceptionType(), errorEntity.exceptionId());
        SubsystemExceptionEntityFields textFields = new ExceptionTextLoader(errorDao).visit(errorEntity.exceptionType(), errorEntity.exceptionId());

        return ErrorDescription.create(
                errorEntity.id(),
                statusCode,
                textFields.stacktrace(),
                textFields.message(),
                errorDetails);
    }

    @Override
    public ErrorDetails synchronisation(ErrorEntity.Action action, Long input) {
        return new SynchronisationErrorDetails();
    }

    @Override
    public ErrorDetails addLocation(ErrorEntity.Action action, Long input) {
        return map(errorDao.getLocationAdd(input));
    }

}
