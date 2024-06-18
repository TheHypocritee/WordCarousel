// WordFlipCardActivity.java
package com.shengda.wordcarousel;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WordCardActivity extends AppCompatActivity {
    private Spinner spinnerGroups;
    private TextView textViewWord;
    private TextView textViewTranslation;
    private Button buttonNext;
    private Button buttonFlip;
    private MyHelper myHelper;
    private List<Word> wordList;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        spinnerGroups = findViewById(R.id.spinnerGroups);
        textViewWord = findViewById(R.id.textViewWord);
        textViewTranslation = findViewById(R.id.textViewTranslation);
        buttonNext = findViewById(R.id.buttonNext);
        buttonFlip = findViewById(R.id.buttonFlip);
        myHelper = new MyHelper(this);

        loadGroups();

        buttonNext.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % wordList.size();
            showWord(currentIndex);
        });

        buttonFlip.setOnClickListener(v -> {
            if (textViewTranslation.getVisibility() == View.VISIBLE) {
                textViewTranslation.setVisibility(View.GONE);
            } else {
                textViewTranslation.setVisibility(View.VISIBLE);
            }
        });

        spinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWords(id+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @SuppressLint("Range")
    private void loadGroups() {
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cursor = db.query("word_groups", new String[]{"group_id", "group_name"}, null, null, null, null, null);
        List<String> groupNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            groupNames.add(cursor.getString(cursor.getColumnIndex("group_name")));
        }
        cursor.close();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroups.setAdapter(adapter);
    }

    private void loadWords(long groupId) {
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cursor = db.query("words", null, "group_id=?", new String[]{String.valueOf(groupId)}, null, null, null);
        wordList = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String english = cursor.getString(cursor.getColumnIndex("english"));
            @SuppressLint("Range") String chinese = cursor.getString(cursor.getColumnIndex("chinese"));
            wordList.add(new Word(english, chinese));
        }
        cursor.close();
        db.close();
        if (!wordList.isEmpty()) {
            currentIndex = 0;
            showWord(currentIndex);
        }
    }

    private void showWord(int index) {
        Word word = wordList.get(index);
        textViewWord.setText(word.getEnglish());
        textViewTranslation.setText(word.getChinese());
        textViewTranslation.setVisibility(View.GONE);
    }

    private class Word {
        private String english;
        private String chinese;

        public Word(String english, String chinese) {
            this.english = english;
            this.chinese = chinese;
        }

        public String getEnglish() {
            return english;
        }

        public String getChinese() {
            return chinese;
        }
    }
}
