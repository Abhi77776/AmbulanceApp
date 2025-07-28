package com.example.ambulanceapp;
import com.example.ambulanceapp.NetworkResponse;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SubmitAlertTask extends AsyncTask<String, Void, NetworkResponse> {

    private final Context context;
    private ProgressDialog progressDialog;

    public SubmitAlertTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Submitting alert...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected NetworkResponse doInBackground(String... params) {
        String hospitalCode = params[0];
        String diseaseEn = params[1];
        String diseaseKa = params[2];
        String hospitalName = params[3];

        try {
            URL url = new URL("http://10.0.2.2:8000/accounts/api/submit-alert/");
            // Use 127.0.0.1:8000 for Postman/backend testing
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject postData = new JSONObject();
            postData.put("hospital_code", hospitalCode);
            postData.put("disease", diseaseEn);  // assuming diseaseEn is like "Dengue"

            OutputStream os = conn.getOutputStream();
            os.write(postData.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                return new NetworkResponse(true, "Success");
            } else {
                return new NetworkResponse(false, "Server error: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new NetworkResponse(false, "Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(NetworkResponse result) {
        super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (!result.success) {
            Toast.makeText(context, "Submission failed: " + result.message, Toast.LENGTH_LONG).show();
        }
    }
}
