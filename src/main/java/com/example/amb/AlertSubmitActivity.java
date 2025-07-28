package com.example.amb;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlertSubmitActivity extends AppCompatActivity {

    private TextView codeTextView, englishTextView, kannadaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_submit); // Ensure this XML exists

        codeTextView = findViewById(R.id.codeTextView);
        englishTextView = findViewById(R.id.englishTextView);
        kannadaTextView = findViewById(R.id.kannadaTextView);

        // Get data from intent
        String code = getIntent().getStringExtra("hospital_code");
        String english = getIntent().getStringExtra("disease_en");
        String kannada = getIntent().getStringExtra("disease_ka");

        codeTextView.setText("Hospital Code: " + code);
        englishTextView.setText("Disease (EN): " + english);
        kannadaTextView.setText("Disease (KA): " + kannada);
    }
}
