// DailyCheckInActivity.java
package com.shengda.wordcarousel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class DailyCheckInActivity extends AppCompatActivity {
    private TextView textViewCheckInStatus;
    private Button buttonCheckIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_check_in);

        textViewCheckInStatus = findViewById(R.id.textViewCheckInStatus);
        buttonCheckIn = findViewById(R.id.buttonCheckIn);
        sharedPreferences = getSharedPreferences("check_in_prefs", MODE_PRIVATE);

        String lastCheckInDate = sharedPreferences.getString("last_check_in_date", "");
        if (lastCheckInDate.equals(getCurrentDate())) {
            textViewCheckInStatus.setText("今日已打卡");
            buttonCheckIn.setEnabled(false);
        } else {
            textViewCheckInStatus.setText("今日未打卡");
            buttonCheckIn.setEnabled(true);
        }

        buttonCheckIn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_check_in_date", getCurrentDate());
            editor.apply();
            textViewCheckInStatus.setText("今日已打卡");
            buttonCheckIn.setEnabled(false);
        });
    }

    private String getCurrentDate() {
        // 返回当前日期，格式为 "yyyy-MM-dd"
        return java.text.DateFormat.getDateInstance().format(new java.util.Date());
    }
}
