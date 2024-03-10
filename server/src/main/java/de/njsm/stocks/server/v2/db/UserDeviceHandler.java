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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.business.data.UserDeviceForPrincipals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserDeviceRecord;
import fj.data.Validation;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class UserDeviceHandler extends CrudDatabaseHandler<UserDeviceRecord, UserDevice> {

    public UserDeviceHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public Validation<StatusCode, List<Identifiable<UserDevice>>> getDevicesOfUser(Identifiable<User> user) {
        return runFunction(context -> {
            List<Identifiable<UserDevice>> result = context
                    .selectFrom(USER_DEVICE)
                    .where(USER_DEVICE.BELONGS_TO.eq(user.id())
                            .and(USER_DEVICE.TECHNICAL_USE_CASE.isNull())
                            .and(nowAsBestKnown()))
                    .fetch()
                    .stream()
                    .map(r -> UserDeviceForPrincipals.builder()
                            .id(r.getId())
                            .build())
                    .collect(Collectors.toList());

            return Validation.success(result);

        });
    }

    public Validation<StatusCode, Boolean> isTechnicalUser(Identifiable<UserDevice> deviceId) {
        return runFunction(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            UserDeviceRecord userDevice = context.selectFrom(USER_DEVICE)
                    .where(USER_DEVICE.ID.eq(deviceId.id())
                            .and(getValidTimeStartField().le(now))
                            .and(now.lt(getValidTimeEndField()))
                            .and(getTransactionTimeEndField().eq(INFINITY)))
                    .fetchOne();

            return Validation.success(userDevice.getTechnicalUseCase() != null);
        });
    }

    @Override
    protected Table<UserDeviceRecord> getTable() {
        return USER_DEVICE;
    }

    @Override
    protected Function<UserDeviceRecord, UserDevice> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> BitemporalUserDevice.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .name(cursor.getName())
                    .belongsTo(cursor.getBelongsTo())
                    .build();
        else
            return cursor -> UserDeviceForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .name(cursor.getName())
                    .belongsTo(cursor.getBelongsTo())
                    .build();
    }

    @Override
    protected TableField<UserDeviceRecord, Integer> getIdField() {
        return USER_DEVICE.ID;
    }

    @Override
    protected TableField<UserDeviceRecord, Integer> getVersionField() {
        return USER_DEVICE.VERSION;
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                USER_DEVICE.ID,
                USER_DEVICE.VERSION,
                USER_DEVICE.NAME,
                USER_DEVICE.BELONGS_TO
        );
    }
}
