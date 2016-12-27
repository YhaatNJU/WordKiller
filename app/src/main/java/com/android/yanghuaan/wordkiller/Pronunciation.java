package com.android.yanghuaan.wordkiller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by YangHuaan on 2016/12/23.
 */

public class Pronunciation {

    private static final String TAG = "Pronunciation";
    private static final String AUDIOPATH = "/data/data/com.android.yanghuaan.wordkiller/files/";

    private Context mContext;
    private SoundPool mSoundPool;
    private int mEnId;
    private int mAmid;

    public Pronunciation(String uuidString) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        load(uuidString, 1);
        load(uuidString, 2);


    }

    private void load(String uuidString, int type){
        String audioName = AUDIOPATH;
        if (type == 1){
            audioName += uuidString + "_E.mp3";
            mEnId = mSoundPool.load(audioName, 1);
        }else if (type == 2){
            audioName += uuidString + "_A.mp3";
            mAmid = mSoundPool.load(audioName, 1);
        }

    }

    public static void pronounce(String uuidString, int type){
        MediaPlayer player = new MediaPlayer();
        String audioName = AUDIOPATH;
        if (type == 1){
            audioName += uuidString + "_E.mp3";
        }else if (type == 2){
            audioName += uuidString + "_A.mp3";
        }
        try {
            player.setDataSource(audioName);
            player.prepare();
            player.start();

        }catch (IOException ioe){
            Log.e(TAG, "无法播放该音频",ioe);
        }
    }

    public void pronounce(int type){
        if (type == 1){
            mSoundPool.play(mEnId,1.0f,1.0f,1,0,1.0f);
        }else{
            mSoundPool.play(mAmid,1.0f,1.0f,1,0,1.0f);
        }
    }

    public void release(){
        mSoundPool.release();
    }
}
