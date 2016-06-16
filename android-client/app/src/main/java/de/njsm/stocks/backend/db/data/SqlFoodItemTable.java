package de.njsm.stocks.backend.db.data;

public class SqlFoodItemTable {

    public static final String NAME;

    public static final String COL_ID;
    public static final String COL_EAT_BY;
    public static final String COL_OF_TYPE;
    public static final String COL_STORED_IN;
    public static final String COL_REGISTERS;
    public static final String COL_BUYS;

    public static final String CREATE;
    public static final String DROP;
    public static final String CLEAR;
    public static final String SELECT_ALL;

    static {
        NAME = "Food_item";
        COL_ID = "ID";
        COL_EAT_BY = "eat_by";
        COL_OF_TYPE = "of_type";
        COL_STORED_IN = "stored_in";
        COL_REGISTERS = "registers";
        COL_BUYS = "buys";
        CREATE = "CREATE TABLE " + NAME + " (\n" +
                "    " + COL_ID + " int UNSIGNED NOT NULL UNIQUE,\n" +
                "    " + COL_EAT_BY + " varchar(19) NOT NULL,\n" +
                "    " + COL_OF_TYPE + " int UNSIGNED NOT NULL,\n" +
                "    " + COL_REGISTERS + " int UNSIGNED NOT NULL,\n" +
                "    " + COL_BUYS + " int UNSIGNED NOT NULL,\n" +
                "    " + COL_STORED_IN + " int UNSIGNED NOT NULL,    \n" +
                "    FOREIGN KEY (" + COL_OF_TYPE + ") REFERENCES " + SqlFoodTable.NAME + "(" + SqlFoodTable.COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "    FOREIGN KEY (" + COL_REGISTERS + ") REFERENCES " + SqlDeviceTable.NAME + "(" + SqlDeviceTable.COL_ID + ") ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                "    FOREIGN KEY (" + COL_BUYS + ") REFERENCES " + SqlUserTable.NAME + "(" + SqlUserTable.COL_ID + ") ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                "    FOREIGN KEY (" + COL_STORED_IN + ") REFERENCES " + SqlLocationTable.NAME + "(" + SqlLocationTable.COL_ID + ") ON DELETE RESTRICT ON UPDATE CASCADE,\n" +
                "    PRIMARY KEY (" + COL_ID + ")\n" +
                ");";
        DROP = "DROP TABLE IF EXISTS " + NAME;
        CLEAR = "DELETE FROM " + NAME;
        SELECT_ALL = "SELECT * FROM " + NAME;
    }
}
