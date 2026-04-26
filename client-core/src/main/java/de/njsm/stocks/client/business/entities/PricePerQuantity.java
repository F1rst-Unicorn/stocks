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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static de.njsm.stocks.client.business.Constants.ROUNDING_PRECISION;

@AutoValue
public abstract class PricePerQuantity {

    public abstract BigDecimal price();

    public abstract BigDecimal scale();

    public abstract StoredAmount quantity();

    @Memoized
    public BigDecimal normalisedPrice() {
        BigDecimal finalScale = quantity().amount().multiply(scale());
        return price().divide(finalScale, ROUNDING_PRECISION, RoundingMode.HALF_UP);
    }

    @Memoized
    public StoredAmount normalisedQuantity() {
        return StoredAmount.create(BigDecimal.ONE, quantity().abbreviation());
    }

    public static PricePerQuantity create(BigDecimal price, StoredAmount quantity) {
        return new AutoValue_PricePerQuantity(price, BigDecimal.ONE, quantity);
    }

    public static PricePerQuantity create(BigDecimal price, BigDecimal scale, StoredAmount quantity) {
        return new AutoValue_PricePerQuantity(price, scale, quantity);
    }
}
