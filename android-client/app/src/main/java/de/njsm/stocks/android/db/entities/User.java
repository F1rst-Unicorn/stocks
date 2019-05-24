package de.njsm.stocks.android.db.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class User extends VersionedData {

    @ColumnInfo(name = "name")
    public String name;

    public User(int id, int version, String name) {
        super(id, version);
        this.name = name;
    }

    @Ignore
    public User(String name) {
        this.name = name;
    }

    @Ignore
    public User(int id, int version) {
        super(id, version);
    }

    @Ignore
    public User() {
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
    @NonNull
    public String toString() {
        return "User (" + id + ", " + version + ", " + name + ")";
    }
}
