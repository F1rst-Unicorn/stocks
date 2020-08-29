package de.njsm.stocks.android.db.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_28_29 extends Migration {

    public Migration_28_29() {
        super(28, 29);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("update Updates set last_update = substr(last_update, -6, - length(last_update) + 6) || '000' || substr(last_update, -1, -5)");
        database.execSQL("update FoodItem set eat_by = substr(eat_by, -6, - length(eat_by) + 6) || '000' || substr(eat_by, -1, -5)");
    }
}
