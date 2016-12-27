package com.android.yanghuaan.wordkiller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by YangHuaan on 2016/12/22.
 */

public class WordPagerActivity extends AppCompatActivity {

    private static final String EXTRA_WORD_ID =
            "com.yanghuaan.android.wordkiller.word_id";

    private ViewPager mViewPager;
    private List<Word> mWords;

    public static Intent newIntent(Context packageContext, UUID uuid){
        Intent intent = new Intent(packageContext, WordPagerActivity.class);
        intent.putExtra(EXTRA_WORD_ID, uuid);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_pager);

        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_WORD_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_word_pager_view_pager);
        mWords = WordList.getWordList(this).getWords();
        FragmentManager manager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {
                Word word = mWords.get(position);
                return WordDetailFragment.newInstance(word.getUUID());
            }

            @Override
            public int getCount() {
                return mWords.size();
            }
        });

        for (int i = 0; i < mWords.size(); i ++){
            if (mWords.get(i).getUUID().equals(uuid)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
