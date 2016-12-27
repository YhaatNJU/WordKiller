package com.android.yanghuaan.wordkiller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * Created by YangHuaan on 2016/12/22.
 */

public class WordQuery {

    private static final String TAG = "WordQuery";
    private static final String API_KEY = "1060337579";
    private static final String KEY_FROM = "WordKiller";
    private static final Uri ENDPOINT = Uri
            .parse("http://fanyi.youdao.com/openapi.do")
            .buildUpon()
            .appendQueryParameter("keyfrom", KEY_FROM)
            .appendQueryParameter("key", API_KEY)
            .appendQueryParameter("type", "data")
            .appendQueryParameter("doctype", "json")
            .appendQueryParameter("version", "1.1")
            .build();
    private Context mContext;

    public WordQuery(Context context){
        mContext = context;
    }

    public Word queryWord(String word){

        if (!isNetworkAvailableAndConnect()){
            return null;
        }
        String url = buildUrl(word);
        Word resultWord = null;
        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            resultWord = parseWord(jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON",je);
        }
        catch (IOException ioe){
            Log.e(TAG, "Failed to fetch word", ioe);
        }

     return resultWord;
    }

    public String buildUrl(String word){
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("q", word);
        return builder.build().toString();
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + "" +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }


    private Word parseWord(JSONObject jsonBody)
        throws IOException, JSONException{

        if (jsonBody.getInt("errorCode") != 0  || !jsonBody.has("basic")){
            return null;
        }

        JSONObject basicJsonBody = jsonBody.getJSONObject("basic");
        UUID uuid = UUID.randomUUID();
        String word = "";
        String meaning = "";
        Date lasted = new Date();
        String pronunciationE = "";
        String pronunciationA = "";
        String example = "";
        word = jsonBody.getString("query");
        JSONArray array = basicJsonBody.getJSONArray("explains");
        int length = array.length();
        for (int i = 0; i < length - 1; i ++){
            meaning += array.get(i).toString() + "\n";
        }
        meaning += array.get(length - 1);
        pronunciationE = basicJsonBody.getString("uk-phonetic");
        pronunciationA = basicJsonBody.getString("us-phonetic");

        Word resultWord = new Word(uuid.toString(),word,
                meaning,pronunciationE,pronunciationA,lasted,example);

        return resultWord;
    }

    public void PronunciationDownload(Word word, Context context){
        String urlE = "http://dict.youdao.com/dictvoice?type=1&audio=" + word.getWord();
        String urlA = "http://dict.youdao.com/dictvoice?type=2&audio=" + word.getWord();

        try {
            byte[] pronunciationE = getUrlBytes(urlE);
            saveAudio(pronunciationE, word.getUUID().toString() + "_E.mp3", context);
            byte[] pronunciationA = getUrlBytes(urlA);
            saveAudio(pronunciationA, word.getUUID().toString() + "_A.mp3" ,context);
        }catch (IOException ioe){
            Log.e(TAG, "Error downloading audio", ioe);
        }
    }

    private void saveAudio(byte[] source, String audioName ,Context context)
            throws IOException{
        OutputStream out = context.openFileOutput(audioName, Context.MODE_PRIVATE);
        out.write(source);
        out.flush();
        out.close();
    }

    private boolean isNetworkAvailableAndConnect(){
        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetWorkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetWorkAvailable
                && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

}
