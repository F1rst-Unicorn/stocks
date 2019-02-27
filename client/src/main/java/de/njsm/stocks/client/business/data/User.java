package de.njsm.stocks.client.business.data;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.client.business.data.visitor.AbstractVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@XmlRootElement
public class User extends VersionedData {

    public String name;

    public User(int id, int version, String name) {
        super(id, version);
        this.name = name;
    }

    public User(String name) {
        this.name = name;
    }

    public User(int id, int version) {
        super(id, version);
    }

    public User() {
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.user(this, arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id &&
                version == user.version &&
                ((name == null && user.name == null) ||
                        (name != null && name.equals(user.name)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, name);
    }

    @Override
    public String toString() {
        return "User (" + id + ", " + version + ", " + name + ")";
    }
}
