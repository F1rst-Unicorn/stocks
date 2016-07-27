package de.njsm.stocks.backend.db.data;

public class SqlUpdateTable {

    public static final String NAME;

    public static final String COL_ID;
    public static final String COL_NAME;
    public static final String COL_DATE;

    public static final String CREATE;
    public static final String INIT;
    public static final String DROP;
    public static final String CLEAR;
    public static final String SELECT_ALL;

    static {
        NAME = "Updates";
        COL_ID = "_id";
        COL_NAME = "name";
        COL_DATE = "last_update";
        CREATE = "CREATE TABLE " + NAME + " (\n" +
                "    " + COL_ID + " int UNSIGNED NOT NULL UNIQUE,\n" +
                "    " + COL_NAME + " varchar(200) NOT NULL,\n" +
                "    " + COL_DATE + " varchar(19) NOT NULL DEFAULT '1000-01-01 00:00:00',\n" +
                "    PRIMARY KEY (" + COL_ID + ")\n" +
                ")";
        INIT = "INSERT INTO Updates (" + COL_ID + ", " + COL_NAME + ")\n" +
                "VALUES\n" +
                "(1, 'Location'),\n" +
                "(2, 'User'),\n" +
                "(3, 'User_device'),\n" +
                "(4, 'Food'),\n" +
                "(5, 'Food_item')";
        DROP = "DROP TABLE IF EXISTS " + NAME;
        CLEAR = "DELETE FROM " + NAME;
        SELECT_ALL = "SELECT * FROM " + NAME;
    }
}
