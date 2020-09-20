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

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.business.data.visitor.BaseVisitor;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static de.njsm.stocks.server.v2.db.jooq.Tables.*;

public class InsertVisitor<T extends Record> extends BaseVisitor<InsertSetStep<T>, InsertOnDuplicateStep<T>> {

    private Principals principals;

    @Override
    public InsertOnDuplicateStep<T> food(Food f, InsertSetStep<T> arg) {
        return arg.columns(FOOD.NAME, FOOD.VERSION, FOOD.EXPIRATION_OFFSET, FOOD.INITIATES)
                .values(f.name, f.version, f.expirationOffset, principals.getDid());
    }

    @Override
    public InsertOnDuplicateStep<T> location(Location l, InsertSetStep<T> arg) {
        return arg.columns(LOCATION.NAME, LOCATION.VERSION, LOCATION.INITIATES)
                .values(l.name, l.version, principals.getDid());
    }

    @Override
    public InsertOnDuplicateStep<T> eanNumber(EanNumber n, InsertSetStep<T> arg) {
        return arg.columns(EAN_NUMBER.NUMBER, EAN_NUMBER.IDENTIFIES, EAN_NUMBER.INITIATES)
                .values(n.eanCode, n.identifiesFood, principals.getDid());
    }

    @Override
    public InsertOnDuplicateStep<T> foodItem(FoodItem i, InsertSetStep<T> input) {
        return input.columns(FOOD_ITEM.EAT_BY,
                FOOD_ITEM.STORED_IN,
                FOOD_ITEM.OF_TYPE,
                FOOD_ITEM.REGISTERS,
                FOOD_ITEM.BUYS,
                FOOD_ITEM.INITIATES)
                .values(OffsetDateTime.from(i.eatByDate.atOffset(ZoneOffset.UTC)),
                        i.storedIn,
                        i.ofType,
                        i.registers,
                        i.buys,
                        principals.getDid());
    }

    @Override
    public InsertOnDuplicateStep<T> userDevice(UserDevice userDevice, InsertSetStep<T> input) {
        return input.columns(USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO, USER_DEVICE.INITIATES)
                .values(userDevice.name, userDevice.userId, principals.getDid());
    }

    @Override
    public InsertOnDuplicateStep<T> user(User u, InsertSetStep<T> arg) {
        return arg.columns(USER.NAME, USER.INITIATES)
                .values(u.name, principals.getDid());
    }

    public void setPrincipals(Principals principals) {
        this.principals = principals;
    }
}
