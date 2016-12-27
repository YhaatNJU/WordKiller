package com.android.yanghuaan.wordkiller.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.android.yanghuaan.wordkiller.DateHelper;
import com.android.yanghuaan.wordkiller.Word;
import com.android.yanghuaan.wordkiller.database.WordSchema.WordTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YangHuaan on 2016/12/22.
 */

public class WordCursorWrapper extends CursorWrapper {

    public WordCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Word getWord(){
        String uuidString = getString(getColumnIndex(WordTable.Cols.UUID));
        String word = getString(getColumnIndex(WordTable.Cols.WORD));
        String meaning = getString(getColumnIndex(WordTable.Cols.MEANING));
        String lasted = getString(getColumnIndex(WordTable.Cols.LASTED));
        String pronunciationE = getString(getColumnIndex(WordTable.Cols.PRONUNCIATION_E));
        String pronunciationA = getString(getColumnIndex(WordTable.Cols.PRONUNCIATION_A));
        String example = getString(getColumnIndex(WordTable.Cols.EXAMPLE));

        Word resultWord = new Word(uuidString,word,meaning,pronunciationE,
                pronunciationA, DateHelper.getDate(lasted),example);

        return resultWord;
    }


}
