package de.njsm.stocks.client.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.client.business.data.visitor.AbstractVisitor;

import javax.xml.bind.annotation.XmlRootElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDevice extends VersionedData {

    public String name;

    public int userId;

    public UserDevice(int id, int version) {
        super(id, version);
    }

    public UserDevice(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    public UserDevice(int id, int version, String name, int userId) {
        super(id, version);
        this.name = name;
        this.userId = userId;
    }

    public UserDevice() {
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.userDevice(this, arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDevice that = (UserDevice) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + userId;
        return result;
    }

    @Override
    public String toString() {
        return "Device (" + id + ", " + name + ")";
    }
}
