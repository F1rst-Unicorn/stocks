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

package de.njsm.stocks.common.data.visitor;

import de.njsm.stocks.common.data.*;
import de.njsm.stocks.common.data.view.UserDeviceView;

import org.threeten.bp.format.DateTimeFormatter;

public class ToStringVisitor extends StocksDataVisitorImpl<Void, String> {

    private final DateTimeFormatter format;

    public ToStringVisitor(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public String food(Food food, Void input) {
        return "\t" + food.id + ": " + food.name;
    }

    @Override
    public String foodItem(FoodItem item, Void input) {
        return "\t\t" + item.id + ": " + format.format(item.eatByDate);
    }

    @Override
    public String user(User u, Void input) {
        return "\t" + u.id + ": " + u.name;
    }

    @Override
    public String userDevice(UserDevice device, Void input) {
        return super.userDevice(device, input);
    }

    @Override
    public String location(Location location, Void input) {
        return "\t" + location.id + ": " + location.name;
    }

    @Override
    public String update(Update update, Void input) {
        return "\t" + update.table + ": " + format.format(update.lastUpdate);
    }

    @Override
    public String ticket(Ticket t, Void input) {
        return super.ticket(t, input);
    }

    @Override
    public String userDeviceView(UserDeviceView device, Void input) {
        return "\t" + device.id + ": " + device.user + "'s " + device.name;
    }

    @Override
    public String eanNumber(EanNumber number, Void input) {
        return "\t" + number.id + ": " + number.eanCode;
    }
}
