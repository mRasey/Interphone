package com.codemine.talk2me;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_CONTRACTS = "create table CONTRACTS ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "time text, "
            + "msg text)";

    public static final String CREATE_CHAT = "create table CHAT ("
            + "id integer primary key autoincrement, "
            + "from text, "
            + "to text, "
            + "msg text, "
            + "time text)";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CONTRACTS);
        sqLiteDatabase.execSQL(CREATE_CHAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
