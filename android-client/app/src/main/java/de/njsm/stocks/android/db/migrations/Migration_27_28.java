package de.njsm.stocks.android.db.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_27_28 extends Migration {

    public Migration_27_28() {
        super(27, 28);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("create table search_suggestion (" +
                "term text primary key not null," +
                "last_queried text not null" +
                ")");
    }
}
