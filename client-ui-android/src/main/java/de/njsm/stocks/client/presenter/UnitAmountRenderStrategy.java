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
import de.njsm.stocks.client.business.entities.UnitAmount;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

public class UnitAmountRenderStrategy {

    @Inject
    public UnitAmountRenderStrategy() {
    }

    public String render(UnitAmount scaledUnit) {
        if (scaledUnit.decimalPrefix().getSymbol().isEmpty())
            return render(scaledUnit.prefixedAmount())
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.abbreviation();
        else
            return render(scaledUnit.prefixedAmount())
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.decimalPrefix().getSymbol()
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.abbreviation();
    }

    public String renderUnitSymbol(UnitAmount scaledUnit) {
        if (scaledUnit.decimalPrefix().getSymbol().isEmpty())
            return scaledUnit.abbreviation();
        else
            return scaledUnit.decimalPrefix().getSymbol()
                    + spaceForFullWordAbbreviation(scaledUnit.abbreviation())
                    + scaledUnit.abbreviation();
    }

    public String render(FullScaledUnitSummaryFields scaledUnit) {
        return String.format("%s (%s)", render((ScaledUnitSummaryFields) scaledUnit), scaledUnit.name());
    }

    public String render(List<? extends UnitAmount> unitAmounts) {
        return unitAmounts.stream()
                .map(this::render)
                .collect(Collectors.joining(", "));
    }

    public String render(BigDecimal number) {
        StringBuffer buffer = new StringBuffer();
        return NumberFormat.getInstance().format(number, buffer, new FieldPosition(0)).toString();
    }

    private String spaceForFullWordAbbreviation(String abbreviation) {
        if (Character.isUpperCase(abbreviation.charAt(0)))
            return " ";
        else
            return "";
    }
}
