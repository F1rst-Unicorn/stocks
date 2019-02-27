package de.njsm.stocks.client.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.client.business.data.visitor.AbstractVisitor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class Data {

    public int id;

    public Data() {}

    public Data(int id) {
        this.id = id;
    }

    public abstract <I, O> O accept(AbstractVisitor<I, O> visitor, I arg);
}
