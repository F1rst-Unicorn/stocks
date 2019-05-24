package de.njsm.stocks.android.db.entities;


import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class Data {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public int id;

    public Data() {}

    public Data(int id) {
        this.id = id;
    }

}
