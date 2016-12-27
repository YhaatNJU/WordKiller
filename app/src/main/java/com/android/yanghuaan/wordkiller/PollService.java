package com.android.yanghuaan.wordkiller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by YangHuaan on 2016/12/23.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final int POLL_INTERNAL = 1000 * 60 * 60 * 1;
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String PREM_PRIVATE =
            "com.android.yanghuaan.wordkiller.PRIVATE";

    public static final String ACTION_SHOW_NOTIFICATION =
            "com.android.yanghuaan.wordkiller.SHOW_NOTIFICATION";

    private int mWarningWords = 0;
    private int mDangerousWords = 0;

    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);
        WordList wordList = WordList.getWordList(this);
        List<Word> words = wordList.getWords();
        find(words);
        if (mWarningWords == 0 && mDangerousWords == 0){
            return;
        }

        Resources resources = getResources();
        Intent i = MainActivity.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(resources.getString(R.string.word_review_title))
                .setSmallIcon(R.mipmap.icon_launcher)
                .setContentTitle(resources.getString(R.string.word_review_title))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        if (mDangerousWords > 0){
            builder.setContentText(resources.getString(R.string.word_review_dangerous_text,
                    mDangerousWords,mWarningWords));
        }else if (mWarningWords > 0){
            builder.setContentText(resources.getString(R.string.word_review_warning_text,
                    mWarningWords));
        }
        Notification notification = builder.build();

        /*NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);

        sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PREM_PRIVATE);*/
        showBackgroundNotification(0, notification);

    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERNAL, pendingIntent);
        }else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void find(List<Word> words){
        for (Word word : words){
            int pastDay = DateHelper.getPastDay(word.getLasted(), new Date());
            if (pastDay >= 5){
                mDangerousWords ++;
                continue;
            }else if (pastDay >= 1){
                mWarningWords ++;
                continue;
            }

        }
    }

    private void showBackgroundNotification(int requestCode, Notification notification){
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(intent, PREM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }
}
