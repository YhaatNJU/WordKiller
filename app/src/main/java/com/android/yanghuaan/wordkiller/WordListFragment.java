package com.android.yanghuaan.wordkiller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

/**
 * Created by YangHuaan on 2016/12/21.
 */

public class WordListFragment extends VisibleFragment {

    private static final String TAG = "WordListFragment";

    private RecyclerView mRecyclerView;
    private WordAdapter mWordAdapter;
    private Word mSuggestedWord;
    private SearchView mSearchView;
    private WordList mWordList;

    public static Fragment newInstance(){
        return new WordListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PollService.setServiceAlarm(getActivity(), true);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_word_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.word_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_word_action_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_add_word);
        mSearchView = (SearchView) searchItem.getActionView();


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "queryTextSubmit: " + query);
                queryWord(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void queryWord(String word){
        Word w = isDownload(word);
        if (w != null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(w.getWord() + "\n" + w.getMeaning());

            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }else {
            FetchWordTask task = new FetchWordTask();
            task.setQuery(word);
            task.execute();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updateUI(){
        mWordList = WordList.getWordList(getActivity());
        List<Word> words = mWordList.getWords();
        if (mWordAdapter == null){
            mWordAdapter = new WordAdapter(words);
            mRecyclerView.setAdapter(mWordAdapter);
        }else {
            mWordAdapter.setWords(words);
            mWordAdapter.notifyDataSetChanged();
        }
    }

    private Word isDownload(String word){

        for (Word w : mWordList.getWords()){
            if (word.equals(w.getWord()))
                return w;
        }

        return null;
    }

    private class WordHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mMeaningTextView;
        private TextView mLastedSkimTextView;
        private Word mWord;

        public WordHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMeaningTextView = (TextView) itemView.findViewById(R.id.list_item_meaning);
            mLastedSkimTextView = (TextView) itemView.findViewById(R.id.list_item_lasted_skim_time);
        }

        public void bindWord(Word word){
            mWord = word;
            mMeaningTextView.setText(mWord.getMeaning());
            mLastedSkimTextView.setText(DateHelper.getString(mWord.getLasted()));
            mLastedSkimTextView.setTextColor(getResources().getColor(R.color.color_safe));
            int dayPast = DateHelper.getPastDay(word.getLasted(), new Date());
            Log.i(TAG, dayPast + "");
            if (dayPast >= 5){
                mLastedSkimTextView.setTextColor(getResources().getColor(R.color.colorDangerous));
            }else if (dayPast >= 1){
                mLastedSkimTextView.setTextColor(getResources().getColor(R.color.color_warning));
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = WordPagerActivity.newIntent(getActivity(), mWord.getUUID());
            startActivity(intent);
        }

    }

    private class WordAdapter extends RecyclerView.Adapter<WordHolder>{

        private List<Word> mWords;

        @Override
        public WordHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_word, parent, false);

            return new WordHolder(view);
        }

        public WordAdapter(List<Word> words){
            mWords = words;
        }

        @Override
        public int getItemCount() {
            return mWords.size();
        }

        @Override
        public void onBindViewHolder(WordHolder holder, int position) {
            Word word = mWords.get(position);
            holder.bindWord(word);
        }

        public void setWords(List<Word> words){
            mWords = words;
        }
    }

    private class FetchWordTask extends AsyncTask<Void, Void, Word>{

        private String mQuery;

        @Override
        protected Word doInBackground(Void... voids) {
            if (mQuery == null){
                return null;
            }else {
                Word word =new WordQuery(getActivity()).queryWord(mQuery);
                return word;
            }
        }

        @Override
        protected void onPostExecute(final Word word) {
            mSuggestedWord = word;
            if (mSuggestedWord != null){
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(word.getWord() + "\n" + word.getMeaning());
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mWordList.addWord(word);
                        mSearchView.setQuery("",false);
                        updateUI();
                        new DownTask().execute();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();
            }else {
                Toast.makeText(getContext(),
                        getResources().getString(R.string.no_word_return), Toast.LENGTH_LONG)
                .show();
            }

        }

        public void setQuery(String query){
            mQuery = query;
        }
    }

    private class DownTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            new WordQuery(getActivity()).PronunciationDownload(mSuggestedWord, getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getContext(), "单词读音下载成功！", Toast.LENGTH_SHORT);
            Log.i(TAG, "单词读音下载成功！");
        }
    }


}
