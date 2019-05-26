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

public class StocksDataVisitorImpl<I, O> implements StocksDataVisitor<I, O> {

    @Override
    public O visit(Data data, I input) {
        return data.accept(this, input);
    }

    @Override
    public O food(Food food, I input) {
        return null;
    }

    @Override
    public O foodItem(FoodItem item, I input) {
        return null;
    }

    @Override
    public O user(User u, I input) {
        return null;
    }

    @Override
    public O userDevice(UserDevice device, I input) {
        return null;
    }

    @Override
    public O location(Location location, I input) {
        return null;
    }

    @Override
    public O update(Update update, I input) {
        return null;
    }

    @Override
    public O ticket(Ticket t, I input) {
        return null;
    }

    @Override
    public O userDeviceView(UserDeviceView userDeviceView, I input) {
        return null;
    }

    @Override
    public O eanNumber(EanNumber number, I input) {
        return null;
    }

    @Override
    public O serverTicket(ServerTicket serverTicket, I input) {
        return null;
    }
}
