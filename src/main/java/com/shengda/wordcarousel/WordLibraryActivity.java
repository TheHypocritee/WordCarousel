// WordLibraryActivity.java
package com.shengda.wordcarousel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class WordLibraryActivity extends AppCompatActivity {
    private EditText editTextEnglish;
    private EditText editTextChinese;
    private Spinner spinnerGroups;
    private LinearLayout linearLayoutWords;
    private Button buttonAddWord;
//    private Button buttonSelectCSV;
    private MyHelper myHelper;
    private static final int REQUEST_CODE_CSV = 1001;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_library);

        editTextEnglish = findViewById(R.id.editTextEnglish);
        editTextChinese = findViewById(R.id.editTextChinese);
        spinnerGroups = findViewById(R.id.spinnerGroups);
        buttonAddWord = findViewById(R.id.buttonAddWord);
//        buttonSelectCSV = findViewById(R.id.buttonSelectCSV);
        linearLayoutWords = findViewById(R.id.linearLayoutWords);
        myHelper = new MyHelper(this);

        loadGroups();
        displayWords();

        buttonAddWord.setOnClickListener(v -> {
            String english = editTextEnglish.getText().toString();
            String chinese = editTextChinese.getText().toString();
            long groupId = spinnerGroups.getSelectedItemId()+1;
            if (!english.isEmpty() && !chinese.isEmpty()) {
                SQLiteDatabase db = myHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("english", english);
                values.put("chinese", chinese);
                values.put("group_id", groupId);
                db.insert("words", null, values);
                db.close();
                Toast.makeText(WordLibraryActivity.this, "单词添加成功", Toast.LENGTH_SHORT).show();
                editTextEnglish.setText("");
                editTextChinese.setText("");
                displayWords();
            } else {
                Toast.makeText(WordLibraryActivity.this, "请输入单词和中文释义", Toast.LENGTH_SHORT).show();
            }
        });

//        buttonSelectCSV.setOnClickListener(v -> {
//            // Open file picker to select CSV file
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("text/csv");
//            startActivityForResult(intent, REQUEST_CODE_CSV);
//        });
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

    private void displayWords() {
        linearLayoutWords.removeAllViews(); // 清空之前的显示
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cursor = db.query("words", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String english = cursor.getString(cursor.getColumnIndex("english"));
                @SuppressLint("Range") String chinese = cursor.getString(cursor.getColumnIndex("chinese"));
                @SuppressLint("Range") int groupId = cursor.getInt(cursor.getColumnIndex("group_id"));
                @SuppressLint("Range") int wordId = cursor.getInt(cursor.getColumnIndex("word_id")); // 获取 word_id

                // 获取单词组名称
                Cursor groupCursor = db.query("word_groups", new String[]{"group_name"}, "group_id=?", new String[]{String.valueOf(groupId)}, null, null, null);
                @SuppressLint("Range") String groupName = groupCursor.moveToFirst() ? groupCursor.getString(groupCursor.getColumnIndex("group_name")) : "未分组";
                groupCursor.close();

                // 创建TextView显示单词
                TextView textView = new TextView(this);
                textView.setText(english + " - " + chinese + " (" + groupName + ")");
                textView.setTextSize(18);

                // 创建删除按钮
                Button deleteButton = new Button(this);
                deleteButton.setText("删除");
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                deleteButton.setLayoutParams(params);

                // 设置word_id为删除按钮的tag
                deleteButton.setTag(wordId);

                deleteButton.setOnClickListener(v -> {
                    // 获取删除按钮的tag作为word_id
                    int deleteWordId = (int) v.getTag();
                    // 在这里执行删除操作
                    deleteWord(deleteWordId);
                });

                // 将TextView和Button添加到LinearLayout中
                LinearLayout wordLayout = new LinearLayout(this);
                wordLayout.setOrientation(LinearLayout.HORIZONTAL);
                wordLayout.addView(textView);
                wordLayout.addView(deleteButton);

                linearLayoutWords.addView(wordLayout);
            }
            cursor.close();
        }
    }

    private void deleteWord(int wordId) {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        db.delete("words", "word_id=?", new String[]{String.valueOf(wordId)});
        db.close(); // 关闭数据库连接
        displayWords(); // 重新显示单词列表
        Toast.makeText(WordLibraryActivity.this, "单词删除成功", Toast.LENGTH_SHORT).show();
    }
//单词书导入功能不完善无法使用
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_CSV && resultCode == RESULT_OK && data != null) {
//            // Process the selected CSV file
//            Uri uri = data.getData();
//            if (uri != null) {
//                try {
//                    InputStream inputStream = getContentResolver().openInputStream(uri);
//                    if (inputStream != null) {
//                        // Read CSV file and insert data into database
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                        String line;
//                        SQLiteDatabase db = myHelper.getWritableDatabase();
//                        int successCount = 0; // 记录成功插入的单词数量
//                        while ((line = reader.readLine()) != null) {
//                            String[] parts = line.split(",");
//                            if (parts.length >= 3) { // 确保有足够的字段
//                                String english = parts[0].trim();
//                                String chinese = parts[1].trim();
//                                long groupId = Long.parseLong(parts[2].trim()); // 假设第三列是group_id
//
//                                ContentValues values = new ContentValues();
//                                values.put("english", english);
//                                values.put("chinese", chinese);
//                                values.put("group_id", groupId);
//
//                                long rowId = db.insert("words", null, values);
//                                if (rowId != -1) { // 插入成功
//                                    successCount++;
//                                }
//                            }
//                        }
//                        db.close();
//                        reader.close();
//                        inputStream.close();
//
//                        // Refresh UI to display newly added words
//                        displayWords();
//                        Toast.makeText(this, "成功导入 " + successCount + " 条单词", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (IOException | NumberFormatException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "导入过程中出现错误，请重试", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

}
