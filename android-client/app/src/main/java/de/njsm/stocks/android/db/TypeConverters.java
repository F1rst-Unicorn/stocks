package de.njsm.stocks.android.db;

import androidx.room.TypeConverter;
import de.njsm.stocks.android.util.Config;
import org.threeten.bp.Instant;

public class TypeConverters {

    @TypeConverter
    public String instantToString(Instant i) {
        return Config.DATABASE_DATE_FORMAT.format(i);
    }

    @TypeConverter
    public Instant stringToInstant(String s) {
        return Config.DATABASE_DATE_FORMAT.parse(s, Instant::from);
    }
}
