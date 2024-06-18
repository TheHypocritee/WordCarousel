// RegisterActivity.java
package com.shengda.wordcarousel;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonBackToLogin;
    private MyHelper myHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myHelper = new MyHelper(this);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);
        // RegisterActivity.java
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                SQLiteDatabase db = myHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("password", password);
                db.insert("users", null, values);
                db.close();

                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            }
        });
        buttonBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
