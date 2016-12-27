package com.android.yanghuaan.wordkiller.database;

/**
 * Created by YangHuaan on 2016/12/21.
 */

public class WordSchema {

    public static final class WordTable{

        public static final String NAME = "words";

        public static final class Cols{

            public static final String UUID = "uuid";
            public static final String WORD = "word";
            public static final String MEANING = "meaning";
            public static final String LASTED = "lasted";
            public static final String PRONUNCIATION_E = "pronunciationE";
            public static final String PRONUNCIATION_A = "pronunciationA";
            public static final String EXAMPLE = "example";

        }

    }
}
