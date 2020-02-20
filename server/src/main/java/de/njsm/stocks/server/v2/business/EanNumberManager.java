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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import fj.data.Validation;

import javax.ws.rs.container.AsyncResponse;
import java.util.stream.Stream;

public class EanNumberManager extends BusinessObject {

    private EanNumberHandler eanNumberHandler;

    public EanNumberManager(EanNumberHandler eanNumberHandler) {
        super(eanNumberHandler);
        this.eanNumberHandler = eanNumberHandler;
    }

    public Validation<StatusCode, Integer> add(EanNumber item) {
        return runFunction(() -> eanNumberHandler.add(item));
    }

    public Validation<StatusCode, Stream<EanNumber>> get(AsyncResponse r) {
        return runAsynchronously(r, () -> {
            eanNumberHandler.setReadOnly();
            return eanNumberHandler.get();
        });
    }

    public StatusCode delete(EanNumber item) {
        return runOperation(() -> eanNumberHandler.delete(item));
    }
}
