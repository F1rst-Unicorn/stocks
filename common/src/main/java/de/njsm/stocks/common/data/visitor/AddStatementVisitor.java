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

public class AddStatementVisitor extends StocksDataVisitorImpl<Void, String> {

    public String visit(Data data) {
        return visit(data, null);
    }

    @Override
    public String food(Food food, Void input) {
        return "INSERT INTO Food (`ID`, name) VALUES (?,?)";
    }

    @Override
    public String foodItem(FoodItem item, Void input) {
        return "INSERT INTO Food_item " +
                "(`ID`, eat_by, of_type, stored_in, registers, buys) VALUES (?,?,?,?,?,?)";
    }

    @Override
    public String user(User u, Void input) {
        return "INSERT INTO User (`ID`, name) VALUES (?,?)";
    }

    @Override
    public String userDevice(UserDevice device, Void input) {
        return "INSERT INTO User_device (`ID`, name, belongs_to) VALUES (?,?,?)";
    }

    @Override
    public String location(Location location, Void input) {
        return "INSERT INTO Location (`ID`, name) VALUES (?,?)";
    }

    @Override
    public String update(Update update, Void input) {
        return "UPDATE Updates SET last_update=? WHERE table_name=?";
    }

    @Override
    public String eanNumber(EanNumber number, Void input) {
        return "INSERT INTO EAN_number (`ID`, number, identifies) VALUES (?,?,?)";
    }

    @Override
    public String ticket(Ticket t, Void input) {
        return "INSERT INTO Ticket (ticket, belongs_device, created_on) VALUES (?, ?, NOW())";
    }
}
