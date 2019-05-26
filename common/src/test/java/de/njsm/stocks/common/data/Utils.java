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

package de.njsm.stocks.common.data;

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class Utils {

    public static StocksDataVisitor<Integer, Integer> getMockVisitor() {
        StocksDataVisitor<Integer,Integer> result = Mockito.mock(StocksDataVisitor.class);
        when(result.food(any(), any())).thenReturn(2);
        when(result.foodItem(any(), any())).thenReturn(2);
        when(result.location(any(), any())).thenReturn(2);
        when(result.user(any(), any())).thenReturn(2);
        when(result.userDevice(any(), any())).thenReturn(2);
        when(result.userDeviceView(any(), any())).thenReturn(2);
        when(result.serverTicket(any(), any())).thenReturn(2);
        when(result.ticket(any(), any())).thenReturn(2);
        when(result.update(any(), any())).thenReturn(2);
        when(result.eanNumber(any(), any())).thenReturn(2);
        return result;
    }


}
