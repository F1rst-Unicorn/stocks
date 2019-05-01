package de.njsm.stocks.client.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.client.business.data.visitor.AbstractVisitor;
import de.njsm.stocks.client.business.json.InstantDeserialiser;
import de.njsm.stocks.client.business.json.InstantSerialiser;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@XmlRootElement
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
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.update(this, arg);
    }
}
