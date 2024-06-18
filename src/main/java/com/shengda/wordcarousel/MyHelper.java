package com.shengda.wordcarousel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "wordcarousel.db";
    private static final int DB_VERSION = 2; // 更新数据库版本

    public MyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE words (word_id INTEGER PRIMARY KEY AUTOINCREMENT, english TEXT NOT NULL, chinese TEXT NOT NULL, group_id INTEGER,FOREIGN KEY (group_id) REFERENCES word_groups(group_id))");
        db.execSQL("CREATE TABLE word_groups (group_id INTEGER PRIMARY KEY AUTOINCREMENT, group_name TEXT NOT NULL)"); // 修改表结构
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS words");
        db.execSQL("DROP TABLE IF EXISTS word_groups");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);

    }
}
