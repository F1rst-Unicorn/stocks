package de.njsm.stocks.android.db.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_29_30 extends Migration {

    public Migration_29_30() {
        super(29, 30);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("update Updates set last_update = last_update || '0'");
        database.execSQL("update FoodItem set eat_by = eat_by || '0'");
    }
}
