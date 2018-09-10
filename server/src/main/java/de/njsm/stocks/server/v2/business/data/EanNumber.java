package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.server.v2.business.data.visitor.AbstractVisitor;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class EanNumber extends VersionedData {

    public String eanCode;

    public int identifiesFood;

    public EanNumber(int id, int version, String eanCode, int identifiesFood) {
        super(id, version);
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    public EanNumber(String eanCode, int identifiesFood) {
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EanNumber eanNumber = (EanNumber) o;
        return id == eanNumber.id &&
                identifiesFood == eanNumber.identifiesFood &&
                Objects.equals(eanCode, eanNumber.eanCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eanCode, identifiesFood);
    }

    @Override
    public String toString() {
        return "EanNumber{" +
                "id=" + id +
                "version=" + version +
                ", eanCode='" + eanCode + '\'' +
                ", identifiesFood=" + identifiesFood +
                ", id=" + id +
                '}';
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.eanNumber(this, arg);
    }
}
