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
public class Food extends VersionedData {

    public String name;

    public Food(int id, String name, int version) {
        super(id, version);
        this.name = name;
    }

    public Food(String name) {
        this.name = name;
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.food(this, arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return id == food.id &&
                version == food.version &&
                Objects.equals(name, food.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version);
    }

    @Override
    public String toString() {
        return "Food (" + id + ", " + name + ", " + version + ")";
    }
}
