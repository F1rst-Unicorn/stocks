package de.njsm.stocks.backend.db.data;

public class SqlLocationTable {

    public static final String NAME;

    public static final String COL_ID;
    public static final String COL_NAME;

    public static final String CREATE;
    public static final String DROP;
    public static final String SELECT_ALL;
    public static final String SELECT_NAME;

    static {
        NAME = "Location";
        COL_ID = "_id";
        COL_NAME = "name";
        CREATE = "CREATE TABLE " + NAME + " (\n" +
                "    " + COL_ID + " int UNSIGNED NOT NULL UNIQUE,\n" +
                "    " + COL_NAME + " varchar(200) NOT NULL,\n" +
                "    PRIMARY KEY (" + COL_ID + ")\n" +
                ")";
        DROP = "DROP TABLE IF EXISTS " + NAME;
        SELECT_ALL = "SELECT * FROM " + NAME;
        SELECT_NAME = "SELECT * FROM " + NAME + " WHERE " + COL_NAME + "=?";
    }
}
