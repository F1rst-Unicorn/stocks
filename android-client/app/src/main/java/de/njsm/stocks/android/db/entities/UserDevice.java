package de.njsm.stocks.android.db.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class UserDevice extends VersionedData {

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "belongs_to")
    public int userId;

    @Ignore
    public UserDevice(int id, int version) {
        super(id, version);
    }

    @Ignore
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

    @NonNull
    @Override
    public String toString() {
        return "Device (" + id + ", " + name + ")";
    }
}
