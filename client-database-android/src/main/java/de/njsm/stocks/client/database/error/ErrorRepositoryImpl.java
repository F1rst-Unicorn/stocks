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
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.UnitDbEntity;
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
                .distinctUntilChanged()
                .map(v -> v.stream().map(this::resolveData).collect(Collectors.toList()));
    }

    @Override
    public Observable<ErrorDescription> getError(long id) {
        return errorDao.observeError(id).map(this::resolveData);
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
        return SynchronisationErrorDetails.create();
    }

    @Override
    public ErrorDetails addLocation(ErrorEntity.Action action, Long input) {
        return map(errorDao.getLocationAdd(input));
    }

    @Override
    public ErrorDetails deleteLocation(ErrorEntity.Action action, Long input) {
        LocationDeleteEntity locationDeleteEntity = errorDao.getLocationDelete(input);
        LocationDbEntity location = errorDao.getLocationByValidOrTransactionTime(locationDeleteEntity.locationId(), locationDeleteEntity.transactionTime());
        return LocationDeleteErrorDetails.create(location.id(), location.name());
    }

    @Override
    public ErrorDetails editLocation(ErrorEntity.Action action, Long input) {
        LocationEditEntity locationEditEntity = errorDao.getLocationEdit(input);
        return LocationEditErrorDetails.create(locationEditEntity.locationId(), locationEditEntity.name(), locationEditEntity.description());
    }

    @Override
    public ErrorDetails addUnit(ErrorEntity.Action action, Long input) {
        UnitAddEntity data = errorDao.getUnitAdd(input);
        return UnitAddForm.create(data.name(), data.abbreviation());
    }

    @Override
    public ErrorDetails deleteUnit(ErrorEntity.Action action, Long input) {
        UnitDeleteEntity unitDeleteEntity = errorDao.getUnitDelete(input);
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(unitDeleteEntity.unitId(), unitDeleteEntity.transactionTime());
        return UnitDeleteErrorDetails.create(unit.id(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails editUnit(ErrorEntity.Action action, Long input) {
        UnitEditEntity unitEditEntity = errorDao.getUnitEdit(input);
        return UnitEditErrorDetails.create(unitEditEntity.unitId(), unitEditEntity.name(), unitEditEntity.abbreviation());
    }

    @Override
    public ErrorDetails addScaledUnit(ErrorEntity.Action action, Long input) {
        ScaledUnitAddEntity scaledUnitAddEntity = errorDao.getScaledUnitAdd(input);
        UnitDbEntity unit = errorDao.getLatestUnitEntity(scaledUnitAddEntity.unit());
        return ScaledUnitAddErrorDetails.create(scaledUnitAddEntity.scale(), scaledUnitAddEntity.unit(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails editScaledUnit(ErrorEntity.Action action, Long input) {
        ScaledUnitEditEntity entity = errorDao.getScaledUnitEdit(input);
        UnitDbEntity unit = errorDao.getLatestUnitEntity(entity.unit());
        return ScaledUnitEditErrorDetails.create(entity.id(), entity.scale(), unit.id(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails deleteScaledUnit(ErrorEntity.Action action, Long input) {
        ScaledUnitDeleteEntity entity = errorDao.getScaledUnitDelete(input);
        return errorDao.getScaledUnit(entity.id(), entity.version());
    }
}
