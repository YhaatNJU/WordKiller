package com.android.yanghuaan.wordkiller.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.yanghuaan.wordkiller.WordList;
import com.android.yanghuaan.wordkiller.database.WordSchema.WordTable;

/**
 * Created by YangHuaan on 2016/12/21.
 */

public class WordBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "wordBase.db";

    public WordBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + WordTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                WordTable.Cols.UUID + ", " +
                WordTable.Cols.WORD + "," +
                WordTable.Cols.MEANING + ", " +
                WordTable.Cols.LASTED + ", " +
                WordTable.Cols.PRONUNCIATION_E + ", " +
                WordTable.Cols.PRONUNCIATION_A + ", " +
                WordTable.Cols.EXAMPLE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
