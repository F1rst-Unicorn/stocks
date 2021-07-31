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

package de.njsm.stocks.common.api.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.api.serialisers.InstantDeserialiser;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        creatorVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class BitemporalData extends VersionedData {

    private final Instant validTimeStart;

    private final Instant validTimeEnd;

    private final Instant transactionTimeStart;

    private final Instant transactionTimeEnd;

    private final int initiates;

    public BitemporalData(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates) {
        super(id, version);
        this.validTimeStart = validTimeStart;
        this.validTimeEnd = validTimeEnd;
        this.transactionTimeStart = transactionTimeStart;
        this.transactionTimeEnd = transactionTimeEnd;
        this.initiates = initiates;
    }

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant getValidTimeStart() {
        return validTimeStart;
    }

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant getValidTimeEnd() {
        return validTimeEnd;
    }

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant getTransactionTimeStart() {
        return transactionTimeStart;
    }

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant getTransactionTimeEnd() {
        return transactionTimeEnd;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getInitiates() {
        return initiates;
    }
}
