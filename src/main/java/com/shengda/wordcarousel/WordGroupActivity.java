package com.shengda.wordcarousel;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Intent;

public class WordGroupActivity extends AppCompatActivity {
    private EditText editTextGroupName;
    private Button buttonCreateGroup;
    private Button buttonDeleteGroup;
    private Button buttonBackToMain;
    private ListView listViewGroups;
    private MyHelper myHelper;
    private long selectedGroupId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_group);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);
        buttonDeleteGroup = findViewById(R.id.buttonDeleteGroup);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        listViewGroups = findViewById(R.id.listViewGroups);

        myHelper = new MyHelper(this);

        displayGroups();
        listViewGroups.setOnItemClickListener((parent, view, position, id) -> selectedGroupId = id);

        buttonCreateGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString();
            if (!groupName.isEmpty()) {
                SQLiteDatabase db = myHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("group_name", groupName);
                db.insert("word_groups", null, values);
                db.close();
                Toast.makeText(WordGroupActivity.this, "单词组创建成功", Toast.LENGTH_SHORT).show();
                editTextGroupName.setText("");
                displayGroups();
            } else {
                Toast.makeText(WordGroupActivity.this, "请输入单词组名称", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDeleteGroup.setOnClickListener(v -> {
            if (selectedGroupId != -1) {
                SQLiteDatabase db = myHelper.getWritableDatabase();
                db.delete("word_groups", "group_id=?", new String[]{String.valueOf(selectedGroupId)});
                db.close();
                Toast.makeText(WordGroupActivity.this, "单词组删除成功", Toast.LENGTH_SHORT).show();
                displayGroups();
                selectedGroupId = -1;
            } else {
                Toast.makeText(WordGroupActivity.this, "请选择要删除的单词组", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(WordGroupActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void displayGroups() {
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT group_id AS _id, group_name FROM word_groups", null);
        String[] from = {"group_name"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        listViewGroups.setAdapter(adapter);
    }
}
