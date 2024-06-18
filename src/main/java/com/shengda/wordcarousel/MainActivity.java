// MainActivity.java
package com.shengda.wordcarousel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonCreateWordGroup;
    private Button buttonWordMarquee;
    private Button buttonCheckIn;
    private Button buttonWordCard;
    private Button buttonWordLibrary;
    private Button buttonBackToLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCreateWordGroup = findViewById(R.id.buttonCreateWordGroup);
        buttonWordMarquee = findViewById(R.id.buttonWordMarquee);
        buttonCheckIn = findViewById(R.id.buttonCheckIn);
        buttonWordCard = findViewById(R.id.buttonWordCard);
        buttonWordLibrary = findViewById(R.id.buttonWordLibrary);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);
        buttonCreateWordGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordGroupActivity.class);
                startActivity(intent);
            }
        });

        buttonWordMarquee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordCarouselActivity.class);
                startActivity(intent);
            }
        });
        buttonCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DailyCheckInActivity.class);
                startActivity(intent);
            }
        });
        buttonWordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordCardActivity.class);
                startActivity(intent);
            }
        });
        buttonWordLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordLibraryActivity.class);
                startActivity(intent);
            }
        });
        buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
