package de.njsm.stocks.android.db.entities;


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
public class EanNumber extends VersionedData {

    @ColumnInfo(name = "number")
    public String eanCode;

    @ColumnInfo(name = "identifies")
    public int identifiesFood;

    public EanNumber(int id, int version, String eanCode, int identifiesFood) {
        super(id, version);
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    @Ignore
    public EanNumber(String eanCode, int identifiesFood) {
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    @Ignore
    public EanNumber() {
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
}
