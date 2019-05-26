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

package de.njsm.stocks.client.frontend.cli.service;

import de.njsm.stocks.client.business.data.*;
import de.njsm.stocks.client.business.data.view.UserDeviceView;
import de.njsm.stocks.client.exceptions.InputException;

import java.util.List;

public class Selector {

    protected ScreenWriter writer;

    protected InputReader reader;

    public Selector(ScreenWriter writer, InputReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public User selectUser(List<User> users, String name) throws InputException {
        return selectData(users, "users");
    }

    public Food selectFood(List<Food> f, String name) throws InputException {
        return selectData(f, "food");
    }

    public FoodItem selectItem(List<FoodItem> items) throws InputException {
        return selectData(items, "food items");
    }

    protected Location selectLocation(List<Location> l, String name) throws InputException {
        return selectData(l, "locations");
    }

    protected UserDeviceView selectDevice(List<UserDeviceView> d, String name) throws InputException {
        return selectData(d, "devices");
    }

    private <T extends Data> T selectData(List<T> list, String dataTypeForMessages) throws InputException {
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 0) {
            throw new InputException("No " + dataTypeForMessages + " found");
        } else {
            writer.printDataList("Found more than one possibility: ", dataTypeForMessages, list);
            int resultId = reader.nextInt("Choose one ", list.get(0).id);
            T result = list.stream().filter(i -> i.id == resultId).findFirst().orElse(null);
            if (result == null) {
                throw new InputException("You did an invalid selection");
            } else {
                return result;
            }
        }
    }
}
