package com.android.yanghuaan.wordkiller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.yanghuaan.wordkiller.database.WordBaseHelper;
import com.android.yanghuaan.wordkiller.database.WordCursorWrapper;
import com.android.yanghuaan.wordkiller.database.WordSchema.WordTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by YangHuaan on 2016/12/21.
 */

public class WordList {

    private static WordList sWordList;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private WordList(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new WordBaseHelper(mContext)
                .getWritableDatabase();
    }

    public static WordList getWordList(Context context){
        if (sWordList == null){
            sWordList = new WordList(context);
        }
        return sWordList;
    }

    public static ContentValues getContentValues(Word word){
        ContentValues values = new ContentValues();
        values.put(WordTable.Cols.UUID, word.getUUID().toString());
        values.put(WordTable.Cols.WORD, word.getWord());
        values.put(WordTable.Cols.MEANING, word.getMeaning());
        values.put(WordTable.Cols.LASTED, DateHelper.getString(word.getLasted()));
        values.put(WordTable.Cols.PRONUNCIATION_E, word.getPronunciationE());
        values.put(WordTable.Cols.PRONUNCIATION_A, word.getPronunciationA());
        values.put(WordTable.Cols.EXAMPLE, word.getExample());

        return values;
    }

    public void addWord(Word word){
        ContentValues values = getContentValues(word);
        mDatabase.insert(WordTable.NAME, null, values);
    }

    public void updateWord(Word word){
        String uuidString = word.getUUID().toString();
        ContentValues values = getContentValues(word);

        mDatabase.update(WordTable.NAME, values,
                WordTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private WordCursorWrapper queryWords(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                WordTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                WordTable.Cols.LASTED
        );
        return new WordCursorWrapper(cursor);
    }

    public List<Word> getWords(){

        List<Word> words = new ArrayList<>();
        WordCursorWrapper cursor = queryWords(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                words.add(cursor.getWord());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return words;
    }

    public Word getWord(UUID uuid){
        WordCursorWrapper cursor = queryWords(
                WordTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );
        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getWord();
        }finally {
            cursor.close();
        }
    }


}
