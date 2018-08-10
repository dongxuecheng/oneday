package com.myday.dong.myday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String CREATE_INFO="create table Info("
            +"id integer primary key autoincrement,"
            +"todo text,"
            +"memo text,"
            +"hour integer,"
            +"minute intege,"
            +"status integer," +
            "alarm integer)";

    private Context myContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
       myContext= context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Info");
        onCreate(sqLiteDatabase);
    }
}
