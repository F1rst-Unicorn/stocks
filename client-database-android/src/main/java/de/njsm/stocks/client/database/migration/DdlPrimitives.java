/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.database.migration;

import android.database.Cursor;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

class DdlPrimitives {

    private final SupportSQLiteDatabase database;

    DdlPrimitives(SupportSQLiteDatabase database) {
        this.database = database;
    }

    void createTable(String table, String primaryKey, String... columns) {
        database.execSQL("create table " + table + " (" +
                String.join(", \n", columns) +
                ", primary key (" + primaryKey + ")" +
        ")");
    }

    void copyTableContent(String source, String destination, String... columns) {
        if (columns.length % 2 != 0) {
            throw new IllegalArgumentException("columns must match in pairs");
        }

        StringBuilder sourceColumns = new StringBuilder();
        StringBuilder destinationColumns = new StringBuilder();
        for (int i = 0; i < columns.length; i += 2) {
            sourceColumns.append(columns[i]);
            sourceColumns.append(", ");
        }
        sourceColumns.deleteCharAt(sourceColumns.length() - 1);
        sourceColumns.deleteCharAt(sourceColumns.length() - 1);

        for (int i = 1; i < columns.length; i += 2) {
            destinationColumns.append(columns[i]);
            destinationColumns.append(", ");
        }
        destinationColumns.deleteCharAt(destinationColumns.length() - 1);
        destinationColumns.deleteCharAt(destinationColumns.length() - 1);

        database.execSQL("insert into " + destination + "(" + destinationColumns + ") "
                + "select " + sourceColumns + " from " + source);
    }

    void dropTable(String name) {
        database.execSQL("drop table " + name);
    }

    void dropView(String name) {
        database.execSQL("drop view " + name);
    }

    void renameTable(String from, String to) {
        database.execSQL("alter table " + from + " rename to " + to);
    }

    void copyTable(String oldName, String newName, String primaryKey, List<String> columnDdl, List<String>  columns) {
        copyTable(oldName, newName, primaryKey, columnDdl.toArray(new String[]{}), columns.toArray(new String[]{}));
    }

    /**
     * https://sqlite.org/lang_altertable.html
     */
    void copyTable(String oldName, String newName, String primaryKey, String[] columnDdl, String[] columns) {
        database.execSQL("PRAGMA foreign_keys=OFF");
        database.execSQL("begin transaction");
        List<SavedObject> objects = saveObjects(oldName);
        var temporaryName = "temporary_migration_table";
        dropObjects(objects);
        createTable(temporaryName, primaryKey, columnDdl);
        copyTableContent(oldName, temporaryName, columns);
        dropTable(oldName);
        renameTable(temporaryName, newName);
        restoreObjects(objects);
        database.execSQL("commit");
        database.execSQL("PRAGMA foreign_keys=ON");
    }

    private void dropObjects(List<SavedObject> objects) {
        for (SavedObject object : objects) {
            if (object.type.equals("view")) {
                dropView(object.name);
            } else {
                throw new IllegalArgumentException("cannot migrate " + object.type);
            }
        }
    }

    private void restoreObjects(List<SavedObject> objects) {
        for (SavedObject object : objects) {
            database.execSQL(object.sql);
        }
    }

    private List<SavedObject> saveObjects(String oldName) {
        List<SavedObject> result = new ArrayList<>();
        try (Cursor cursor = database.query("select type, sql, name " +
                "from sqlite_schema " +
                "where sql like '%" + oldName + "%' " +
                "and type in ('view')")) {
            while(cursor.moveToNext()) {
                result.add(new SavedObject(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }
        }
        return result;
    }

    private static class SavedObject {
        private final String type;
        private final String sql;
        private final String name;

        private SavedObject(String type, String sql, String name) {
            this.type = type;
            this.sql = sql;
            this.name = name;
        }
    }

    void renameColumn(String table, String from, String to) {
        database.execSQL("alter table " + table + " rename column " + from + " to " + to);
    }

    void dropIndex(String name) {
        database.execSQL("drop index " + name);
    }

    void createIndex(String table, String name, String where, String... columns) {
        database.execSQL("create index " + name + " on " + table
                + "(" + String.join(", ", columns) + ") where " + where);
    }
}
