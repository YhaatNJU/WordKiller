package com.android.yanghuaan.wordkiller;

import android.app.SearchableInfo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by YangHuaan on 2016/12/21.
 */

public class Word {

    private String mWord;
    private String mMeaning;
    private String mPronunciationE;
    private String mPronunciationA;
    private Date mLasted;
    private UUID mUUID;
    private String mExample;

    public Word(String uuidString,String word, String meaning, String pronunciation_e, String pronunciationA, Date lasted, String example) {
        mUUID = UUID.fromString(uuidString);
        mWord = word;
        mMeaning = meaning;
        mPronunciationE = pronunciation_e;
        mPronunciationA = pronunciationA;
        mLasted = lasted;
        mExample = example;
    }

    public Date getLasted() {
        return mLasted;
    }

    public void setLasted(Date lasted) {
        mLasted = lasted;
    }

    public String getWord() {
        return mWord;
    }

    public String getMeaning() {
        return mMeaning;
    }

    public String getPronunciationE() {
        return mPronunciationE;
    }

    public String getPronunciationA() {
        return mPronunciationA;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getExample() {
        return mExample;
    }

    @Override
    public String toString() {
        String resultString = "";
        resultString += "UUID: " + mUUID.toString();
        resultString += "\nWord: " + mWord;
        resultString += "\nMeaning: " + mMeaning ;
        resultString += "\nLasted: " + DateHelper.getString(mLasted);
        resultString += "\npronunciationE: " + mPronunciationE;
        resultString += "\nPronunciationA: " + mPronunciationA;
        resultString += "\nExample: " + mExample;

        return resultString;

    }


}
