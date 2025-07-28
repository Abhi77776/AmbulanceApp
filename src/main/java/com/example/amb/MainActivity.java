package com.example.amb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Spinner emergencySpinner;
    private Button submitBtn;
    private EditText hospitalCodeInput;

    public String selectedEnglish = "";
    public String selectedKannada = "";
    public String selectedHospitalName = "";

    private static final String TAG = "MainActivity";

    final Map<String, String> emergencyMap = new LinkedHashMap<String, String>() {{
        put("Fever - ಜ್ವರ", "Fever|ಜ್ವರ");
        put("Cough - ಕೆಮ್ಮು", "Cough|ಕೆಮ್ಮು");
        put("Chest Pain - ಎದೆ ನೋವು", "Chest Pain|ಎದೆ ನೋವು");
        put("Diabetes -ಮಧುಮೇಹ", "Diabetes|ಮಧುಮೇಹ");
        put("Hypertension - ಅಧಿಕ ರಕ್ತದೊತ್ತಡ", "Hypertension|ಅಧಿಕ ರಕ್ತದೊತ್ತಡ");
        put("Heart Attack - ಹೃದಯಾಘಾತ", "Heart Attack|ಹೃದಯಾಘಾತ");
        put("Stroke - ಪಾರ್ಶ್ವವಾಯು", "Stroke|ಪಾರ್ಶ್ವವಾಯು");
        put("Fracture - ಮೂಳೆ ಮುರಿತ", "Fracture|ಮೂಳೆ ಮುರಿತ");
        put("Burn Injury - ಸುಟ್ಟ ಗಾಯ", "Burn Injury|ಸುಟ್ಟ ಗಾಯ");
        put("Accident Trauma - ಅಪಘಾತ ಗಾಯ", "Accident Trauma|ಅಪಘಾತ ಗಾಯ");
    }};

    final Map<String, String> hospitalNameCache = new HashMap<String, String>() {{
        put("HOSP01", "McGann Teaching District Hospital");
        put("HOSP02", "Nanjappa Hospital");
        put("HOSP03", "Shivamogga Institute of Medical Sciences (SIMS)");
        put("HOSP04", "Subbaiah Institute of Medical Sciences Hospital");
        put("HOSP05", "Shanthinilaya Multispeciality Hospital");
        put("HOSP06", "Sagar Hospitals");
        put("HOSP07", "Apoorva Multi-speciality Hospital");
        put("HOSP08", "Ashwini Hospital");
        put("HOSP09", "Shivamogga Heart Care and Research Centre");
        put("HOSP10", "Kushal Multi Speciality Hospital");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emergencySpinner = findViewById(R.id.emergencySpinner);
        submitBtn = findViewById(R.id.submitBtn);
        hospitalCodeInput = findViewById(R.id.hospitalCodeInput);

        setupEmergencySpinner();
        setupSubmitButton();
    }

    private void setupEmergencySpinner() {
        List<String> spinnerList = new ArrayList<>(emergencyMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergencySpinner.setAdapter(adapter);

        emergencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String key = spinnerList.get(position);
                String fullValue = emergencyMap.get(key);
                if (fullValue != null) {
                    String[] parts = fullValue.split("\\|");
                    selectedEnglish = parts[0];
                    selectedKannada = parts.length > 1 ? parts[1] : "";
                    Log.d(TAG, "Spinner Selected: English - " + selectedEnglish + ", Kannada - " + selectedKannada);
                } else {
                    selectedEnglish = "";
                    selectedKannada = "";
                    Log.w(TAG, "Spinner Selected: Null value for key - " + key);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEnglish = "";
                selectedKannada = "";
                Log.d(TAG, "Spinner: Nothing selected");
            }
        });
    }

    private void setupSubmitButton() {
        submitBtn.setOnClickListener(v -> {
            String rawHospitalCode = hospitalCodeInput.getText().toString();
            String hospitalCode = rawHospitalCode.trim().toUpperCase();

            Log.d(TAG, "Submit Clicked. Input: " + rawHospitalCode);
            Log.d(TAG, "Processed: " + hospitalCode);
            Log.d(TAG, "Selected Emergency: " + selectedEnglish);

            if (TextUtils.isEmpty(hospitalCode)) {
                Toast.makeText(MainActivity.this, "Please enter the hospital code.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(selectedEnglish)) {
                Toast.makeText(MainActivity.this, "Please select an emergency.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!hospitalNameCache.containsKey(hospitalCode)) {
                Toast.makeText(MainActivity.this, "Hospital code not found. Please check.", Toast.LENGTH_LONG).show();
                return;
            }

            selectedHospitalName = hospitalNameCache.get(hospitalCode);
            Log.i(TAG, "Launching MapActivity for " + selectedHospitalName);

            launchMapForHospital(hospitalCode);
        });
    }

    private void launchMapForHospital(String hospitalCode) {
        Log.d(TAG, "Launching MapActivity with code: " + hospitalCode);
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("com.example.amb.HOSPITAL_CODE", hospitalCode);
        startActivity(intent);
    }
}
