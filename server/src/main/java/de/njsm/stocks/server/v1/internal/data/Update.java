package de.njsm.stocks.server.v1.internal.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
import de.njsm.stocks.server.v2.business.json.InstantSerialiser;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({ "id" })
public class Update extends Data {

    public String table;

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant lastUpdate;

    public Update(String table, Instant lastUpdate) {
        this.table = table;
        this.lastUpdate = lastUpdate;
    }

    public Update() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.update(this, input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Update update = (Update) o;

        if (!table.equals(update.table)) return false;
        return lastUpdate.equals(update.lastUpdate);
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + lastUpdate.hashCode();
        return result;
    }
}
