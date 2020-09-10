package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.db.entities.UserDevice;

public class UserDeviceWithUser extends UserDevice {

    @ColumnInfo(name = "username")
    private String username;

    public UserDeviceWithUser(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, String name, int userId, String username) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, name, userId);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
