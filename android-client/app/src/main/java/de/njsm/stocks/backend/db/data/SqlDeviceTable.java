package de.njsm.stocks.backend.db.data;

public class SqlDeviceTable {

    public static final String NAME;

    public static final String COL_ID;
    public static final String COL_NAME;
    public static final String COL_USER;

    public static final String CREATE;
    public static final String DROP;
    public static final String CLEAR;
    public static final String SELECT_ALL;
    public static final String SELECT_USER;

    static {
        NAME = "User_device";
        COL_ID = "ID";
        COL_NAME = "name";
        COL_USER = "belongs_to";
        CREATE = "CREATE TABLE " + NAME + " (\n" +
                "    " + COL_ID + " int UNSIGNED NOT NULL UNIQUE,\n" +
                "    " + COL_NAME + " varchar(200) NOT NULL,\n" +
                "    " + COL_USER + " int UNSIGNED NOT NULL,\n" +
                "    PRIMARY KEY (" + COL_ID + ")\n" +
                "    CONSTRAINT `device_points_to_user` FOREIGN KEY (" + COL_USER +
                ") REFERENCES " + SqlUserTable.NAME + "(" + SqlUserTable.COL_ID +
                ") ON DELETE RESTRICT ON UPDATE CASCADE\n" +
                ")";
        DROP = "DROP TABLE IF EXISTS " + NAME;
        CLEAR = "DELETE FROM " + NAME;
        SELECT_ALL = "SELECT * FROM " + NAME;
        SELECT_USER = "SELECT * FROM " + NAME + " WHERE " + COL_USER + "=?";
    }
}
