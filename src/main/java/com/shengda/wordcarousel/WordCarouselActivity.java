package com.shengda.wordcarousel;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordCarouselActivity extends AppCompatActivity {
    private Spinner spinnerGroups;
    private Spinner spinnerPronunciation;
    private TextView textViewWord;
    private TextView textViewInterval;
    private SeekBar seekBarInterval;
    private Button buttonStart;
    private Button buttonStop;
    private TextToSpeech textToSpeech;
    private MyHelper myHelper;
    private List<String[]> words; // 改成List<String[]>，存储英文和中文单词
    private int currentIndex = 0;
    private int interval = 1500; // 默认间隔时间为1500毫秒
    private boolean isRunning = false; // 用于控制轮播是否在运行

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_carousel);

        spinnerGroups = findViewById(R.id.spinnerGroups);
        spinnerPronunciation = findViewById(R.id.spinnerPronunciation);
        textViewWord = findViewById(R.id.textViewWord);
        textViewInterval = findViewById(R.id.textViewInterval);
        seekBarInterval = findViewById(R.id.seekBarInterval);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        myHelper = new MyHelper(this);

        loadGroups();
        setupPronunciationSpinner();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US); // 默认美音
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(WordCarouselActivity.this, "语言不支持", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBarInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interval = progress;
                textViewInterval.setText("间隔时间: " + interval + "毫秒");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        buttonStart.setOnClickListener(v -> {
            loadWords();
            startCarousel();
        });

        buttonStop.setOnClickListener(v -> stopCarousel());
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

    private void setupPronunciationSpinner() {
        List<String> pronunciationOptions = new ArrayList<>();
        pronunciationOptions.add("美音");
        pronunciationOptions.add("英音");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pronunciationOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPronunciation.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private void loadWords() {
        long groupId = spinnerGroups.getSelectedItemId() + 1;
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cursor = db.query("words", new String[]{"english", "chinese"}, "group_id=?", new String[]{String.valueOf(groupId)}, null, null, null);
        words = new ArrayList<>();
        while (cursor.moveToNext()) {
            String english = cursor.getString(cursor.getColumnIndex("english"));
            String chinese = cursor.getString(cursor.getColumnIndex("chinese"));
            words.add(new String[]{english, chinese}); // 存储英文和中文单词
        }
        cursor.close();
        db.close();
    }

    private void startCarousel() {
        currentIndex = 0;
        if (words.isEmpty()) {
            Toast.makeText(this, "当前组没有单词", Toast.LENGTH_SHORT).show();
            return;
        }
        isRunning = true; // 开始时设置为true
        runCarousel();
    }

    private void runCarousel() {
        if (isRunning && currentIndex < words.size()) {
            String[] wordPair = words.get(currentIndex);
            String english = wordPair[0];
            String chinese = wordPair[1];
            textViewWord.setText(english + " \n " + chinese);
            speakWord(english); // 只读英文单词
            currentIndex++;
            if (currentIndex >= words.size()) {
                currentIndex = 0; // 循环播放，从头开始
            }
            textViewWord.postDelayed(this::runCarousel, interval); // 使用用户选择的间隔时间
        }
    }

    private void stopCarousel() {
        isRunning = false; // 停止时设置为false
        textViewWord.removeCallbacks(this::runCarousel); // 移除任何未完成的回调
        textToSpeech.stop(); // 停止任何正在进行的TTS
        Toast.makeText(this, "轮播停止", Toast.LENGTH_SHORT).show();
    }

    private void speakWord(String word) {
        String selectedPronunciation = (String) spinnerPronunciation.getSelectedItem();
        if ("美音".equals(selectedPronunciation)) {
            textToSpeech.setLanguage(Locale.US);
        } else if ("英音".equals(selectedPronunciation)) {
            textToSpeech.setLanguage(Locale.UK);
        }
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
