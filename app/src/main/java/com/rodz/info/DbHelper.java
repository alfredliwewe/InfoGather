package com.rodz.info;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, name VARCHAR, phone VARCHAR, email VARCHAR, type VARCHAR, file VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS settings (name VARCHAR, value VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, type VARCHAR, content VARCHAR, date VARCHAR, status TEXT, refer VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS questionnaires (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, title VARCHAR, description VARCHAR, date VARCHAR, status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS questions (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, type VARCHAR, question VARCHAR, name VARCHAR, units TEXT, status VARCHAR, source VARCHAR, parent VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS series (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, name VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS series_data (id INTEGER PRIMARY KEY AUTOINCREMENT, webid VARCHAR, series VARCHAR, name VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS answers (id INTEGER PRIMARY KEY AUTOINCREMENT, form VARCHAR, question VARCHAR, name VARCHAR, answer VARCHAR, row_id VARCHAR, uploaded VARCHAR)");
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}