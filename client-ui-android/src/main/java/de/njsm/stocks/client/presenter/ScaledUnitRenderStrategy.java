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

package de.njsm.stocks.client.presenter;

import de.njsm.stocks.client.business.entities.FullScaledUnitSummaryFields;
import de.njsm.stocks.client.business.entities.ScaledUnitSummaryFields;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.NumberFormat;

public class ScaledUnitRenderStrategy {

    @Inject
    public ScaledUnitRenderStrategy() {
    }

    public String render(ScaledUnitSummaryFields scaledUnit) {
        if (scaledUnit.unitPrefix().getSymbol().isEmpty())
            return toLocaleString(scaledUnit.prefixedScale())
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.abbreviation();
        else
            return toLocaleString(scaledUnit.prefixedScale())
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.unitPrefix().getSymbol()
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.abbreviation();
    }

    public String render(FullScaledUnitSummaryFields scaledUnit) {
        return String.format("%s (%s)", render((ScaledUnitSummaryFields) scaledUnit), scaledUnit.name());
    }

    private String spaceForFullWordAbbreviation(String abbreviation) {
        if (Character.isUpperCase(abbreviation.charAt(0)))
            return " ";
        else
            return "";
    }

    private String toLocaleString(BigDecimal number) {
        StringBuffer buffer = new StringBuffer();
        return NumberFormat.getInstance().format(number, buffer, new FieldPosition(0)).toString();
    }
}
