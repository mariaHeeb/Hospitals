package com.example.hospitals.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UserDatabase {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveUserLogin(String id, String password) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, id);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

        database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public String[] getLastLogin() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToLast(); // Move to the last record
            String[] loginData = new String[2];
            loginData[0] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            loginData[1] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
            cursor.close();
            return loginData;
        }
        return null; // Return null if no records are found
    }
    public void clearLoginData() {
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }
}