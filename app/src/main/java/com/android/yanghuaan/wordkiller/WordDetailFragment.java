package com.android.yanghuaan.wordkiller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

/**
 * Created by YangHuaan on 2016/12/22.
 */

public class WordDetailFragment extends Fragment {

    private static final String ARG_WORD_UUID = "word_uuid";

    private TextView mWordTextView;
    private TextView mUkPronTextView;
    private ImageView mUkPronImageView;
    private TextView mUsPronTextView;
    private ImageView mUsPronImageView;
    private TextView mMeaningTextView;
    private TextView mExampleTextView;
    private ImageView mCheatImageView;
    private EditText mTestEditText;
    private TextView mLastedTextView;
    private Word mWord;
    private WordList mWordList;
    //private Pronunciation mPronunciation;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid = (UUID) getArguments().getSerializable(ARG_WORD_UUID);
        mWordList = WordList.getWordList(getActivity());
        mWord =mWordList.getWord(uuid);
        //mPronunciation = new Pronunciation(mWord.getUUID().toString());

    }

    @Override
    public void onPause() {
        super.onPause();
        WordList.getWordList(getActivity()).updateWord(mWord);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_detail,container,false);

        mWordTextView = (TextView) view.findViewById(R.id.detail_word_text_view);
        if (DateHelper.getPastHour(mWord.getLasted(), new Date()) <= 1){
            mWordTextView.setText(mWord.getWord());
        }else {
            mWordTextView.setText("??????");
        }


        mUkPronTextView = (TextView) view.findViewById(R.id.detail_English_pronunciation_text_view);
        mUkPronTextView.setText("[" + mWord.getPronunciationE() + "]");

        mUkPronImageView = (ImageView) view.findViewById(R.id.detail_English_pronunciation_image_view);
        mUkPronImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mPronunciation.pronounce(1);
                Pronunciation.pronounce(mWord.getUUID().toString(),1);
            }
        });

        mUsPronTextView = (TextView) view.findViewById(R.id.detail_American_pronunciation_text_view);
        mUsPronTextView.setText("[" + mWord.getPronunciationA() + "]");

        mUsPronImageView = (ImageView) view.findViewById(R.id.detail_American_pronunciation_image_view);
        mUsPronImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mPronunciation.pronounce(2);
                Pronunciation.pronounce(mWord.getUUID().toString(), 2);
            }
        });

        mMeaningTextView = (TextView) view.findViewById(R.id.detail_meaning_text_view);
        mMeaningTextView.setText(mWord.getMeaning());

        mExampleTextView = (TextView) view.findViewById(R.id.detail_example_text_view);
        mExampleTextView.setText(mWord.getExample());

        mCheatImageView = (ImageView) view.findViewById(R.id.detail_cheat_image_view);
        mCheatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(getContext());
                builder.setMessage("这样不好吧(∩_∩)");
                builder.setPositiveButton("管它呢", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mWordTextView.setText(mWord.getWord());
                        mWord.setLasted(new Date());
                        mWordList.updateWord(mWord);
                        dialogInterface.dismiss();
                        mCheatImageView.setVisibility(View.INVISIBLE);
                        mTestEditText.setVisibility(View.INVISIBLE);
                    }
                });
                builder.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        mTestEditText = (EditText) view.findViewById(R.id.detail_test_edit_text);
        mTestEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals(mWord.getWord())){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("太棒了！拼写正确！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mWordTextView.setText(mWord.getWord());
                            mWord.setLasted(new Date());
                            mWordList.updateWord(mWord);
                            dialogInterface.dismiss();
                        }
                    }
                    );
                    builder.create().show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLastedTextView = (TextView) view.findViewById(R.id.detail_lasted_skim_text_view);
        mLastedTextView.setText(DateHelper.getString(mWord.getLasted()));
        int dayPast = DateHelper.getPastDay(mWord.getLasted(), new Date());
        if (dayPast >= 5){
            mLastedTextView.setTextColor(getResources().getColor(R.color.colorDangerous));
        }else if (dayPast >= 1){
            mLastedTextView.setTextColor(getResources().getColor(R.color.color_warning));
        }

        return view;
    }

    public static WordDetailFragment newInstance(UUID uuid){
        Bundle args = new Bundle();
        args.putSerializable(ARG_WORD_UUID, uuid);
        WordDetailFragment fragment = new WordDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mPronunciation.release();
    }



}
